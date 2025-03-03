package org.api_sync.services.lista_precios;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.api_sync.adapter.inbound.request.ItemListaPreciosRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.adapter.outbound.entities.*;
import org.api_sync.adapter.outbound.repository.ArticuloRepository;
import org.api_sync.adapter.outbound.repository.ListaDePreciosRepository;
import org.api_sync.adapter.outbound.repository.PrecioRepository;
import org.api_sync.adapter.outbound.repository.ProveedorRepository;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.api_sync.services.lista_precios.mappers.ListaPreciosMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@RequiredArgsConstructor
public class ListaPreciosService {
	private static final List<String> EXPECTED_COLUMNS = List.of("numero", "nombre", "importe_neto", "iva");

	private final ListaDePreciosRepository listaDePreciosRepository;
	private final ArticuloRepository articuloRepository;
	private final ListaPreciosMapper listaDePreciosMapper;
	private final PrecioRepository precioRepository;
	private final ProveedorRepository proveedorRepository;
	

	public ListaPreciosDTO crearListaDePrecios(ListaPreciosRequest request) {
		ListaPrecios listaDePrecios = new ListaPrecios();
		listaDePrecios.setFechaCreacion(LocalDate.now());
		listaDePrecios.setFechaModificacion(LocalDate.now());
		
		if (!proveedorRepository.existsById(request.getProveedor())) {
			throw new RuntimeException("Proveedor no encontrado");
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
														.codUnidadMedida(itemRequest.getCodUnidadMedida())
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
		listaDePreciosRepository.save(listaDePrecios);
		return listaDePreciosMapper.toDTO(listaDePrecios);
	}
	
	public List<ListaPreciosDTO> listarListasDePrecios() {
		return listaDePreciosRepository.findAll().stream()
				       .map(listaDePreciosMapper::toDTO)
				       .collect(Collectors.toList());
	}

	public void procesarArchivo(MultipartFile file, Long proveedorId) {
		try (Reader reader = new InputStreamReader(file.getInputStream())) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT
					                              .withDelimiter(';')         // Usa ';' como separador
					                              .withFirstRecordAsHeader()  // Usa la primera fila como encabezado
					                              .withIgnoreHeaderCase()     // Ignora mayúsculas/minúsculas
					                              .withTrim()                 // Elimina espacios en blanco adicionales
					                              .parse(reader);
			List<ItemListaPreciosRequest> articulos = new ArrayList<>();
			
			for (CSVRecord record : records) {
				validarColumnas(record); // Validar si las columnas son las correctas
				ItemListaPreciosRequest articulo = mapearAtributos(record); // Mapear los datos a la clase Articulo
				articulos.add(articulo);
			}
			
			
			crearListaDePrecios(ListaPreciosRequest.builder().items(articulos).proveedor(proveedorId).build());
			
			// Aquí puedes hacer lo que necesites con los datos procesados, como guardarlos en la base de datos
		} catch (Exception e) {
			throw new RuntimeException("Error al procesar el archivo CSV", e);
		}
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
		articulo.setDescripcion(getOrEmpty(record, "marca"));
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
	
}
