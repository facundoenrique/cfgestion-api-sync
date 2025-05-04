package org.api_sync.services.afip;

import lombok.Builder;

@Builder
public class CaeResponse {
	private String cae;
	private String caeFechaVto;
	private String mesaggeError;
	private String codeError;
	private String message;

}
