package br.com.itau.servico;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.itau.config.ClienteNotFoundException;
import br.com.itau.dto.TransferenciaDTO;
import br.com.itau.modelo.Cliente;
import br.com.itau.modelo.OperacaoType;
import br.com.itau.modelo.Transferencia;
import br.com.itau.repositorio.TransferenciaRepository;
import br.com.itau.util.AppMensage;

@Service
public class TransferenciaService {

	@Autowired
	TransferenciaRepository transferenciaRepository;
 
	@Autowired
	AppMensage appMensage;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Transferencia registraTransferencia(TransferenciaDTO dto, Boolean sucesso, String observacao) {
		Transferencia transferenciaOrigem = OperacaoType.DEBITO.buildTransferencia(dto.numeroContaOrigem(),
				dto.numeroContaDestino(), dto.getValor(), sucesso, observacao);

		Transferencia transferenciaDestino = OperacaoType.CREDITO.buildTransferencia(dto.numeroContaDestino(),
				dto.numeroContaOrigem(), dto.getValor(), sucesso, observacao);

		transferenciaOrigem = transferenciaRepository.save(transferenciaOrigem);
		transferenciaDestino = transferenciaRepository.save(transferenciaDestino);
		return transferenciaOrigem;
	}

	public List<Transferencia> extrato(Long numeroConta) {
		List<Transferencia> list = transferenciaRepository.findByContaOrigemOrderByDataDesc(numeroConta);
		if (list.isEmpty()) {
			throw new ClienteNotFoundException(appMensage.getNenhumRegistroLocalizado());
		}
		return list;

	}

}