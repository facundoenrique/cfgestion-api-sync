package org.api_sync.adapter.inbound.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AfipEventResponse {
	private int code;
	private String message;
}