package org.api_sync.services.lista_precios;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.lista_precios.dto.CvsDTO;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExportPriceListService {

	private final ListaPreciosService listaPreciosService;

	public Optional<CvsDTO> generateCsv(Long id) {
		
		Optional<ListaPreciosDTO> lista = listaPreciosService.getListaPrecio(id);
		
		if (!lista.isPresent()) {
			return Optional.empty();
		}
		
		StringWriter stringWriter = new StringWriter();
		try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
			List<String[]> data = lista.get().getItems().stream().map(item->
				new String[]{
						item.getNumero(),
						item.getNombre(),
						String.valueOf(item.getImporte())
			}).toList();
			csvWriter.writeAll(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.of(new CvsDTO(stringWriter.toString(), lista.get().getNombre()));
	}

}
