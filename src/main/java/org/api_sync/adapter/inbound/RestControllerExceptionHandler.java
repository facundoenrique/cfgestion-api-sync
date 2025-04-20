package org.api_sync.adapter.inbound;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestControllerExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				                                               errors.put(error.getField(), error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleDuplicateKeyException(DataIntegrityViolationException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST, e.getLocalizedMessage()
		);
		problemDetail.setTitle("Conflicto de datos");
		problemDetail.setType(URI.create(e.getMessage()));
		
		problemDetail.setProperty("timestamp", System.currentTimeMillis());
		problemDetail.setProperty("debugInfo", "Consulta los logs para más detalles.");
		
		return problemDetail;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleDuplicateKeyException(RuntimeException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST, e.getMessage()
		);
		problemDetail.setTitle("Conflicto de datos");
		problemDetail.setType(URI.create(e.getMessage()));
		
		problemDetail.setProperty("timestamp", System.currentTimeMillis());
		problemDetail.setProperty("debugInfo", "Consulta los logs para más detalles.");
		
		return problemDetail;
	}
}
