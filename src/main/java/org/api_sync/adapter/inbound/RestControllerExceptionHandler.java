package org.api_sync.adapter.inbound;

import org.api_sync.services.exceptions.PedidoNotOwnedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

	@ExceptionHandler(PedidoNotOwnedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ProblemDetail handlePedidoNotOwnedException(PedidoNotOwnedException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.FORBIDDEN, e.getMessage()
		);
		problemDetail.setTitle("Acceso denegado");
		problemDetail.setType(URI.create("about:blank"));
		
		problemDetail.setProperty("timestamp", System.currentTimeMillis());
		problemDetail.setProperty("error", "PedidoNotOwnedException");
		
		return problemDetail;
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

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", ex.getClass().getSimpleName());
		body.put("message", ex.getMessage());
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		body.put("stackTrace", sw.toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

}
