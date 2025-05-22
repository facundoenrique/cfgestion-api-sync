package org.api_sync.adapter.inbound.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AfipErrorResponse {
	private int code;
	private String message;
}
