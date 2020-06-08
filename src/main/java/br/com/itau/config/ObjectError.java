package br.com.itau.config;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor

@Getter
public class ObjectError implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String message;
	private final String field;
	private final Object parameter;
}