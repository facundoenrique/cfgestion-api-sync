package org.api_sync.adapter.outbound.entities.gestion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CondicionIva {
	RESPONSABLE_INSCRIPTO(1, "Responsable inscripto", "RI"),
	CONSUMIDOR_FINAL(2, "Consumidor final", "CF"),
	MONOTRIBUTO(3, "Monotributo", "MON"),
	EXENTO(4, "Exento", "EX"),
	NO_CATEGORIZADO(5, "No Categorizado", "NC"),
	MONOTRIBUTO_SOCIAL(6, "Monotributo social", "SO");
	
	private int codigo;
	private String nombre;
	private String abreviatura;

	public int getCondicionIVAReceptorId(CondicionIva condicionIva) {
		switch (condicionIva.codigo) {
			case 1: return 1; //Responsable inscripto (FA)
			case 2: return 5; //Consumidor final
			case 3: return 6; //Responsable monotributo FA)
			case 4: return 4; //Exento (FB y FC)
			case 5: return 7; //No categorizado (FB y FC)
			case 6: return 13; //Monotributo social (FB y FC)
			default: return 0;
		}
	}
	
}
