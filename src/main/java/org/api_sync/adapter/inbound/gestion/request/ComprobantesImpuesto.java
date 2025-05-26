package org.api_sync.adapter.inbound.gestion.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class ComprobantesImpuesto {
	private Integer codImpuesto;
    private double importe;
    private double neto;
}
