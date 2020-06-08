package br.com.itau.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class AppMensage {

	@Value("${mensagem.valida.contaIgual}")
	private String contaIgual;

	@Value("${mensagem.valida.valorInvalido}")
	private String valorInvalido;
	
	@Value("${mensagem.valida.saldoInsuficiente}")
	private String saldoInsuficiente;

	@Value("${mensagem.valida.valorMaximoTransferencia}")
	private String valorMaximoTransferencia;

	@Value("${mensagem.valida.nenhumRegistroLocalizado}")
	private String nenhumRegistroLocalizado;

	@Value("${mensagem.valida.nenhumClientePelaConta}")
	private String nenhumClientePelaConta;
	
	@Value("${mensagem.valida.recursoAtualizado}")
	private String recursoAtualizado;

	@Value("${mensagem.erro.badRequest}")
	private String badRequest;

	@Value("${mensagem.erro.notFound}")
	private String notFound;
	
	@Value("${mensagem.erro.conflict}")
	private String conflict;
	
	@Value("${mensagem.transferenciaSucesso}")
	private String transferenciaSucesso;

	@Value("${mensagem.erro.internalServerError}")
	private String internalServerError;

}
