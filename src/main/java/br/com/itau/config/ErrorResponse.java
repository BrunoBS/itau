package br.com.itau.config;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter @Setter
public class ErrorResponse implements Serializable {
	private static final long serialVersionUID = 1L;

    private final String message;
    private final int code;
    private final String status;
    private final List<ObjectError> errors;
}