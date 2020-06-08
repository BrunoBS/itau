package br.com.itau.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundException extends ClienteException {
	private static final long serialVersionUID = 1L;

	public ClienteNotFoundException(String message) {
		super(message, "", "");
	}

	public ClienteNotFoundException(String message, Object parametro, String field) {
		super(message, parametro, field);
	}

}