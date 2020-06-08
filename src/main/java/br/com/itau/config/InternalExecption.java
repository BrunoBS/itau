package br.com.itau.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalExecption extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InternalExecption(String message) {
		super(message);
	}

}