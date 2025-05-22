package org.api_sync.services.afip.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AfipEventResponse {
	private int code;
	private String message;
}