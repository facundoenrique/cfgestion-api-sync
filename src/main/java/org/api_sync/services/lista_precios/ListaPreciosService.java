package org.api_sync.services.lista_precios;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.inbound.request.ItemListaPreciosRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosUpdateRequest;
import org.api_sync.adapter.outbound.entities.*;
import org.api_sync.adapter.outbound.repository.*;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.mappers.ArticuloMapper;
import org.api_sync.services.exceptions.ListaPreciosNotFoundException;
import org.api_sync.services.exceptions.ProveedorNotFoundException;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.api_sync.services.lista_precios.mappers.ListaPreciosMapper;
import org.api_sync.services.lista_precios.utils.CSVUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListaPreciosService {
	private static final List<String> EXPECTED_COLUMNS = List.of("numero", "nombre", "importe_neto", "iva");

	private final ListaPreciosRepository listaPreciosRepository;
	private final ArticuloRepository articuloRepository;
	private final ArticuloMapper articuloMapper;
	private final ListaPreciosMapper listaDePreciosMapper;
	private final PrecioRepository precioRepository;
	private final ProveedorRepository proveedorRepository;
	private final ItemListaPreciosRepository itemListaPreciosRepository;
	

	public ListaPreciosDTO crearListaDePrecios(ListaPreciosRequest request) {
		ListaPrecios listaDePrecios = new ListaPrecios();
		listaDePrecios.setFechaCreacion(LocalDate.now());
		listaDePrecios.setFechaModificacion(LocalDate.now());
		listaDePrecios.setNombre(request.getNombre());
		
		if (!proveedorRepository.existsById(request.getProveedor())) {
			throw new ProveedorNotFoundException();
		}
		
		Proveedor proveedor = proveedorRepository.findById(request.getProveedor()).get();
		
		listaDePrecios.setProveedor(proveedor);
		
		List<ItemListaPrecios> items = request.getItems().stream().map(itemRequest -> {
			Articulo articulo = articuloRepository.findByNumero(itemRequest.getNumero())
					                    .orElseGet(() -> articuloRepository.save(
												Articulo.builder()
														.numero(itemRequest.getNumero())
														.nombre(itemRequest.getNombre())
														.iva(itemRequest.getIva())
														.codUnidadMedida(itemRequest.getCodUnidadMedida() != null ?
																                 itemRequest.getCodUnidadMedida() :  1)
														.build()));
			
			Precio nuevoPrecio = Precio.builder()
					                     .articulo(articulo)
					                     .importe(itemRequest.getImporte())
					                     .fechaVigencia(LocalDate.now())
					                     .build();
			
			Precio precioGuardado = precioRepository.save(nuevoPrecio);
			
			return ItemListaPrecios.builder()
					       .listaPrecios(listaDePrecios)
					       .articulo(articulo)
					       .precio(precioGuardado)
					       .build();
		}).collect(Collectors.toList());
	
		listaDePrecios.setItems(items);
		log.info("Nombre lista: {}", listaDePrecios.getNombre());
		
		listaPreciosRepository.save(listaDePrecios);
		return listaDePreciosMapper.toDTO(listaDePrecios);
	}
	
	public Optional<ListaPreciosDTO> getListaPrecio(Long id) {
		Optional<ListaPrecios> lista = listaPreciosRepository.findById(id);
		if (lista.isPresent()) {
			return Optional.of(listaDePreciosMapper.toDTO(lista.get()));
		}
		return Optional.empty();
	}
	
	public Page<ListaPreciosDTO> listarListasDePrecios(LocalDate fechaDesde,
	                                                   LocalDate fechaHasta,
	                                                   Long proveedorId,
													   String nombre,
	                                                   Pageable pageable) {
		Specification<ListaPrecios> spec = Specification.where(null);
		
		if (fechaDesde != null) {
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaDesde));
		}
		if (fechaHasta != null) {
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaCreacion"), fechaHasta));
		}
		if (proveedorId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("proveedor").get("id"), proveedorId));
		}
		if (StringUtils.isNotBlank(nombre)) {
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
		}
		
		Page<ListaPrecios> listaPreciosPage = listaPreciosRepository.findAll(spec, pageable);
		
		return listaPreciosPage.map(listaDePreciosMapper::toDTO);
	}

	public void procesarArchivo(MultipartFile file, Long proveedorId, String nombreLista) {
		
		try (InputStream inputStream = file.getInputStream()) {
			
			char separador = CSVUtils.detectarSeparador(inputStream);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			Iterable<CSVRecord> records = CSVFormat.DEFAULT
					                              .withDelimiter(separador)         // Usa "separador" como separador
					                              .withFirstRecordAsHeader()  // Usa la primera fila como encabezado
					                              .withIgnoreHeaderCase()     // Ignora mayúsculas/minúsculas
					                              .withTrim()                 // Elimina espacios en blanco adicionales
					                              .parse(reader);
			List<ItemListaPreciosRequest> articulos = new ArrayList<>();
			
			for (CSVRecord record : records) {
				validarColumnas(record); // Validar si las columnas son las correctas
				ItemListaPreciosRequest articulo = mapearAtributos(record); // Mapear los datos a la clase Articulo
				if (validarArticulo(articulo)) {
					articulos.add(articulo);
				} else {
					System.out.println("Articulo " + articulo.getNumero() + "no tiene los datos correctos");
				}
			}
			
			
			ListaPreciosRequest listaPreciosRequest = new ListaPreciosRequest();
			listaPreciosRequest.setItems(articulos);
			listaPreciosRequest.setProveedor(proveedorId);
			listaPreciosRequest.setNombre(nombreLista);
			crearListaDePrecios(listaPreciosRequest);
			
		} catch (Exception e) {
			throw new RuntimeException("Error al procesar el archivo CSV", e);
		}
	}
	
	private boolean validarArticulo(ItemListaPreciosRequest articulo) {
		if (articulo.getNumero() == null) {
			return false;
		}
		if (articulo.getNombre() == null) {
			return false;
		}
		if (articulo.getImporte() == null) {
			return false;
		}
		if (articulo.getIva() == null) {
			return false;
		}
		return true;
	}
	
	private void validarColumnas(CSVRecord record) {
		// Compara las columnas en el encabezado del CSV con las esperadas
		for (String column : EXPECTED_COLUMNS) {
			if (!record.toMap().containsKey(column)) {
				throw new IllegalArgumentException("Columna no encontrada: " + column);
			}
		}
	}

	private ItemListaPreciosRequest mapearAtributos(CSVRecord record) {
		ItemListaPreciosRequest articulo = new ItemListaPreciosRequest();
		articulo.setNumero(record.get("numero"));
		articulo.setNombre(record.get("nombre"));
		articulo.setImporte(new BigDecimal(record.get("importe_neto")));
		articulo.setIva(new BigDecimal(record.get("iva")));
		articulo.setDescripcion(getOrEmpty(record, "descripcion"));
		articulo.setMarca(getOrEmpty(record, "marca"));
		articulo.setCodUnidadMedida(getIntegerOrDefault(record, "cod_unidad_medida"));
		return articulo;
	}
	
	private String getOrEmpty(CSVRecord csvRecord, String key) {
		try {
			if (csvRecord.get(key) == null) {
				return EMPTY;
			}
			return csvRecord.get(key);
		} catch (Exception e) {
			return EMPTY;
		}
	}

	private Integer getIntegerOrDefault(CSVRecord csvRecord, String key) {
		try {
			if (csvRecord.get(key) == null) {
				return 0;
			}
			return Integer.parseInt(csvRecord.get(key));
		} catch (Exception e) {
			return 0;
		}
	}


	public ListaPreciosDTO actualizarListaPrecios(Long id, ListaPreciosUpdateRequest updateRequest) {
		ListaPrecios listaPrecios = listaPreciosRepository.findById(id)
				                            .orElseThrow(() -> new ListaPreciosNotFoundException());
		
		// Actualizar el nombre de la lista de precios
		listaPrecios.setNombre(updateRequest.getNombre());
		
		// Verificar si el proveedor existe
		Proveedor proveedor = proveedorRepository.findById(updateRequest.getProveedor())
				                      .orElseThrow(() -> new ProveedorNotFoundException());
		
		// Actualizar el proveedor de la lista de precios
		listaPrecios.setProveedor(proveedor);
		
		// Guardar los cambios
		return listaDePreciosMapper.toDTO(listaPreciosRepository.save(listaPrecios));
	}

	public ArticuloDTO addItem(ArticuloRequest articuloRequest, Long listId) {
		return saveItemWithList(articuloRequest, listId);
	}

	private ArticuloDTO saveItemWithList(ArticuloRequest articuloRequest, Long listId) {
		
		ListaPrecios listaPrecios = listaPreciosRepository.findById(listId)
				                            .orElseThrow(() -> new ListaPreciosNotFoundException());
		
		Optional<Articulo> itemOptional = articuloRepository.findByNumero(articuloRequest.getNumero());
		
		Articulo item;
		if (itemOptional.isPresent()) {
			//Tendria que validar que no este insertado en la lista;
			if (listaPrecios.getItems().stream().anyMatch(
					i -> i.getArticulo().getNumero().equals(articuloRequest.getNumero()))) {
				//lo actualizo ? que hago ? deberia actualizar el precio.
			}
			item = itemOptional.get();
		} else {
			Articulo articulo = articuloMapper.toEntity(articuloRequest);
			articulo.setFechaCreado(Date.from(Instant.now()));
			item = articuloRepository.save(articulo);
		}
		
		Precio precio = Precio.builder()
				                .articulo(item)
				                .importe(articuloRequest.getPrecio())
				                .fechaVigencia(LocalDate.now())
				                .build();
		
		Precio precioInserted = precioRepository.save(precio);
		
		ItemListaPrecios itemListaPrecios = ItemListaPrecios.builder()
				                                    .articulo(item)
				                                    .precio(precioInserted)
				                                    .listaPrecios(listaPrecios)
				                                    .build();
		
		itemListaPreciosRepository.save(itemListaPrecios);
		
		return articuloMapper.toDTO(item);
	}

}
