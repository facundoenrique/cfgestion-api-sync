package org.api_sync.services.lista_precios.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVUtils {

	public static char detectarSeparador(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String primeraLinea = reader.readLine(); // Leer la primera línea del archivo
		
		if (primeraLinea == null) {
			throw new IOException("El archivo está vacío.");
		}
		
		// Caracteres comunes como delimitadores en CSV
		char[] posiblesSeparadores = {',', ';', '\t', '|'};
		Map<Character, Integer> contador = new HashMap<>();
		
		// Contar ocurrencias de cada separador
		for (char sep : posiblesSeparadores) {
			String regex = Pattern.quote(String.valueOf(sep)); // Evita problemas con caracteres especiales
			int ocurrencias = primeraLinea.split(regex, -1).length - 1; // Cuenta separadores correctamente
			contador.put(sep, ocurrencias);
		}
		// Elegir el separador con más ocurrencias
		return contador.entrySet().stream()
				       .max(Map.Entry.comparingByValue())
				       .orElseThrow(() -> new IOException("No se pudo determinar el separador."))
				       .getKey();
	}
}

