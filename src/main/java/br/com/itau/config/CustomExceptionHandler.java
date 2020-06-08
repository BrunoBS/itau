package br.com.itau.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.itau.util.AppMensage;


@ControllerAdvice
@ResponseBody
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	@Autowired
	AppMensage AppMensage;

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	private ErrorResponse getErrorResponse(String mensage, HttpStatus status, List<ObjectError> errors) {
		return ErrorResponse.builder().message(mensage).code(status.value()).status(status.getReasonPhrase())
				.errors(errors).build();
	}

	private List<ObjectError> getErrors(MethodArgumentNotValidException ex) {
		return ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ObjectError(error.getDefaultMessage(), error.getField(), error.getRejectedValue()))
				.collect(Collectors.toList());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ObjectError> errors = getErrors(ex);
		ErrorResponse errorResponse = getErrorResponse(AppMensage.getBadRequest(), status, errors);
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(LockException.class)
	public final ResponseEntity<ErrorResponse> handleLockException(LockException ex, WebRequest request) {
		ObjectError details = new ObjectError(ex.getMessage(), "", "");
		ErrorResponse errorResponse = getErrorResponse(AppMensage.getConflict(), HttpStatus.CONFLICT,
				Arrays.asList(details));
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InternalExecption.class)
	public final ResponseEntity<ErrorResponse> handleInternalExecption(InternalExecption ex, WebRequest request) {
		ObjectError details = new ObjectError(ex.getMessage(), "", "");
		ErrorResponse errorResponse = getErrorResponse(AppMensage.getInternalServerError(), HttpStatus.INTERNAL_SERVER_ERROR,
				Arrays.asList(details));
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ClienteException.class)
	public final ResponseEntity<ErrorResponse> handleClienteException(ClienteException ex, WebRequest request) {
		return HandleEx(ex, HttpStatus.BAD_REQUEST, AppMensage.getBadRequest());
	}

	@ExceptionHandler(ClienteNotFoundException.class)
	public final ResponseEntity<ErrorResponse> handleClienteNotFoundException(ClienteNotFoundException ex,
			WebRequest request) {
		return HandleEx(ex, HttpStatus.NOT_FOUND, AppMensage.getNotFound());
	}

	private ResponseEntity<ErrorResponse> HandleEx(ClienteException ex, HttpStatus status, String mensagem) {
		ObjectError details = new ObjectError(ex.getMessage(), ex.getField(), ex.getParametro());
		ErrorResponse errorResponse = getErrorResponse(mensagem, status, Arrays.asList(details));
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public final ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		List<ObjectError> details = ex.getConstraintViolations().parallelStream()
				.map(error -> new ObjectError(error.getMessage(), error.getPropertyPath().toString(),
						error.getInvalidValue()))
				.collect(Collectors.toList());

		ErrorResponse error = getErrorResponse(AppMensage.getBadRequest(), HttpStatus.BAD_REQUEST, details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}