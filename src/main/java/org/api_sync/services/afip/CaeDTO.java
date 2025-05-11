package org.api_sync.services.afip;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CaeDTO {
	private String cae;
	private String caeFechaVto;
	private String messageError;
	private String codeError;
	private String message;

}
