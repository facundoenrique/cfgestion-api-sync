package org.api_sync.services.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemNotFoundException extends RuntimeException {
	private String numero;
}
