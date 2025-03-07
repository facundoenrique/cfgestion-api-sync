package org.api_sync.adapter.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
	private T body;
	private String status;
}
