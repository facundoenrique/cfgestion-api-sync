package org.api_sync.services.lista_precios;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PrecioRequest;
import org.api_sync.adapter.outbound.entities.RedArticulo;
import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.api_sync.adapter.outbound.entities.Precio;
import org.api_sync.adapter.outbound.repository.ArticuloRepository;
import org.api_sync.adapter.outbound.repository.ItemListaPreciosRepository;
import org.api_sync.adapter.outbound.repository.PrecioRepository;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.api_sync.services.lista_precios.mappers.ItemListaPreciosMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ItemListaPreciosService {
	private final ItemListaPreciosRepository itemListaPreciosRepository;
	private final PrecioRepository precioRepository;
	private final ItemListaPreciosMapper itemListaPreciosMapper;
	private final ArticuloRepository articuloRepository;
	
	public ItemListaPreciosDTO get(Long id) {
		ItemListaPrecios dto = itemListaPreciosRepository.findById(id)
				                       .orElseThrow(() -> new RuntimeException("ItemListaPrecios no encontrado"));
		return itemListaPreciosMapper.toDTO(dto);
	}
	
	@Transactional
	public ItemListaPreciosDTO actualizarPrecio(Long itemListaPrecioId, PrecioRequest precioRequest) {
		ItemListaPrecios itemListaPrecios = itemListaPreciosRepository.findById(itemListaPrecioId)
				                                    .orElseThrow(() -> new RuntimeException("ItemListaPrecios no encontrado"));
		
		if (itemListaPrecios.getArticulo().getIva().compareTo(precioRequest.getIva()) != 0) {
			RedArticulo articulo = itemListaPrecios.getArticulo();
			articulo.setIva(precioRequest.getIva());
			RedArticulo guardado = articuloRepository.save(articulo);
			itemListaPrecios.setArticulo(guardado);
		}
		
		if (itemListaPrecios.getPrecio().getImporte().compareTo(precioRequest.getImporte()) == 0) {
			//Es el mismo, no actualizamos.
			return itemListaPreciosMapper.toDTO(itemListaPrecios);
		}
		
		Precio nuevoPrecio = Precio.builder()
				                     .importe(precioRequest.getImporte())
				                     .fechaVigencia(LocalDate.now())
				                     .articulo(itemListaPrecios.getArticulo())
				                     .build();
		
		Precio precioGuardado = precioRepository.save(nuevoPrecio);
		itemListaPrecios.setPrecio(precioGuardado);
		return itemListaPreciosMapper.toDTO(itemListaPreciosRepository.save(itemListaPrecios));
	}
}
