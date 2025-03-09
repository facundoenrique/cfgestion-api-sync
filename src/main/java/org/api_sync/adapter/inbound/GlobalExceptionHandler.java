package org.api_sync.adapter.inbound;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail  handleDuplicateKeyException(DataIntegrityViolationException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST, "El CUIT ya está registrado."
		);
		problemDetail.setTitle("Conflicto de datos");
		problemDetail.setType(URI.create("/proveedores"));

		problemDetail.setProperty("timestamp", System.currentTimeMillis());
		problemDetail.setProperty("debugInfo", "Consulta los logs para más detalles.");
		
		return problemDetail;
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleRuntimeException(RuntimeException e) {
		return Map.of("error", e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getAllErrors()
				                      .stream()
				                      .map(error -> ((FieldError) error).getDefaultMessage())
				                      .toList();
		
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}

