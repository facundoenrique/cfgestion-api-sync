package org.api_sync.services.afip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteAfipIva {
	private int id;
	private double baseImp;
	private double importe;
}
