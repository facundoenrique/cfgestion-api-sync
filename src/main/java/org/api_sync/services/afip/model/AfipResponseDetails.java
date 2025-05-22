package org.api_sync.services.afip.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AfipResponseDetails {
	private List<AfipErrorResponse> errors;
	private List<AfipEventResponse> events;
}
