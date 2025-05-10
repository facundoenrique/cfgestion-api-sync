package org.api_sync.adapter.inbound.responses;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CaeResponse {
    private String cae;
    private String caeFechaVto;
    private String messageError;
    private String codeError;
    private String message;
}
