package org.api_sync.services.lista_precios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.lista_precios.dto.CvsDTO;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
        try (CSVPrinter printer = new CSVPrinter(stringWriter, CSVFormat.DEFAULT)) {
            // Escribir fila a fila para minimizar memoria
            lista.get().getItems().forEach(item -> {
                try {
                    printer.printRecord(item.getNumero(), item.getNombre(), String.valueOf(item.getImporte()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return Optional.of(new CvsDTO(stringWriter.toString(), lista.get().getNombre()));
    }

}
