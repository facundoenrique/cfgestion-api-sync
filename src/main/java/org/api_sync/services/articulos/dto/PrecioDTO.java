package org.api_sync.services.articulos.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PrecioDTO {
	private Long id;

	private Long articuloId;
	
	private BigDecimal importe;
	
	private BigDecimal iva;
	
	private LocalDate fechaVigencia;
}
