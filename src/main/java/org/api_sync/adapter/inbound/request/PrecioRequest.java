package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrecioRequest {
	@DecimalMin(value = "0.0", message = "El importe debe ser mayor o igual a 0")
	BigDecimal importe;
	@DecimalMin(value = "0.0", message = "El IVA debe ser mayor o igual a 0")
	BigDecimal iva;
}
