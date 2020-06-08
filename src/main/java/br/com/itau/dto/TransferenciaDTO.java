package br.com.itau.dto;

import java.math.BigDecimal;

import br.com.itau.config.ClienteException;
import br.com.itau.modelo.Cliente;
import br.com.itau.util.AppMensage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TransferenciaDTO {
	private static final BigDecimal LIMITE = new BigDecimal(1000);
	private final BigDecimal valor;
	private final Cliente contaOrigem;
	private final Cliente contaDestino;

	public Long numeroContaOrigem() {
		return contaOrigem.getNumeroConta();
	}

	public Long numeroContaDestino() {
		return contaDestino.getNumeroConta();
	}

	public void transfe() {
		contaOrigem.transferePara(valor, contaDestino);
	}

	public void validaConta(AppMensage appMensage) {
		if (contaOrigem.equals(contaDestino)) {
			throw new ClienteException(appMensage.getContaIgual(), contaOrigem.getNumeroConta(), "Numero da Conta");
		}

		if (valor == null || BigDecimal.ZERO.compareTo(valor) >= 0) {
			throw new ClienteException(appMensage.getValorInvalido(), valor, "valor");

		}
		if (LIMITE.compareTo(valor) < 0) {
			throw new ClienteException(appMensage.getValorMaximoTransferencia(), valor, "valor");
		}

		if (valor.compareTo(contaOrigem.getSaldo()) > 0) {
			throw new ClienteException(appMensage.getSaldoInsuficiente(), valor, "valor");
		}

	}

}
