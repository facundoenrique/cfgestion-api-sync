package org.api_sync.adapter.inbound.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AfipResponseDetails {
	private List<AfipErrorResponse> errors;
	private List<AfipEventResponse> events;
}
