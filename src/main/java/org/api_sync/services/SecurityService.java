package org.api_sync.services;

import org.springframework.stereotype.Service;

@Service
public class SecurityService {
	public boolean check(String company, Integer sucursal) {
		//TODO: Matchear empresa con sucursal. y deberiamos ver session?
		return true;
	}
}
