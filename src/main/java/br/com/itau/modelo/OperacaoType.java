package br.com.itau.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperacaoType {
	CREDITO("Crédito") {
		@Override
		public Transferencia buildTransferencia(Long origem, Long destino, BigDecimal valor, Boolean sucesso,String observacao) {
			return  Transferencia.builder()
					.contaOrigem(origem)
					.contaDestino(destino)
					.data(LocalDateTime.now())
					.valor(valor)
					.sucesso(sucesso)
					.observacao(observacao)
					.operacaoType(this).build();
			
		}
	},
	DEBITO("Débito") {
		@Override
		public Transferencia buildTransferencia(Long origem, Long destino, BigDecimal valor, Boolean sucesso,String observacao) {
			return  Transferencia.builder()
					.contaOrigem(origem)
					.contaDestino(destino)
					.data(LocalDateTime.now())
					.valor(valor)
					.sucesso(sucesso)
					.observacao(observacao)
					.operacaoType(this).build();
			
		}
	};

	private String displayName;

	OperacaoType(String displayName) {
		this.displayName = displayName;
	}

	public abstract Transferencia buildTransferencia(Long origem, Long destino, BigDecimal valor, Boolean sucesso,String observacao);

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}