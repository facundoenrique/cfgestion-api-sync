package org.api_sync.adapter.inbound.responses;

import lombok.Builder;
import lombok.Value;
import org.api_sync.services.afip.model.AfipResponseDetails;

@Builder
@Value
public class CaeResponse {
    private String cae;
    private String caeFechaVto;
    private AfipResponseDetails responseDetails;
}
