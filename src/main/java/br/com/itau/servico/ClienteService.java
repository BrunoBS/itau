package br.com.itau.servico;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.itau.config.ClienteException;
import br.com.itau.config.ClienteNotFoundException;
import br.com.itau.modelo.Cliente;
import br.com.itau.modelo.Transferencia;
import br.com.itau.repositorio.ClienteRepository;
import br.com.itau.util.AppMensage;
import br.com.itau.util.dto.TransferenciaDTO;

@Service
public class ClienteService {

	private static final Long FATOR_CONTA = 1000L;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private TransferenciaService transferenciaService;

	@Autowired
	private AppMensage appMensage;

	public List<Cliente> findAll() {
		List<Cliente> list = clienteRepository.findAll();
		if (list.isEmpty()) {
			throw new ClienteNotFoundException(appMensage.getNenhumRegistroLocalizado());
		}
		return list;
	}

	public Cliente findByConta(Long numeroConta) {
		return clienteRepository.findByNumeroConta(numeroConta).map(s -> s)
				.orElseThrow(() -> new ClienteNotFoundException(appMensage.getNenhumRegistroLocalizado()));
	}

	public Cliente findByContaParaTransferencia(Long numeroConta) {
		return clienteRepository.findByNumeroContaOrderByIdDesc(numeroConta).map(s -> s)
				.orElseThrow(() -> new ClienteNotFoundException(appMensage.getNenhumClientePelaConta(), numeroConta,
						"numeroContaDestino"));
	}

	@Transactional
	public Cliente save(Cliente cliente) {
		cliente.setNumeroConta(
				clienteRepository.findTop1ByOrderByIdDesc().map(s -> s.getId() + FATOR_CONTA).orElse(FATOR_CONTA));
		return clienteRepository.save(cliente);
	}

	@Transactional
	public Transferencia transfere(BigDecimal valor, Long numeroContaOrigem, Long numeroContaDestino) {
		TransferenciaDTO dto = geraTransferencia(valor, numeroContaOrigem, numeroContaDestino);
		try {
			dto.validaConta(appMensage);
			dto.transfe();
			clienteRepository.save(dto.getContaOrigem());
			clienteRepository.save(dto.getContaDestino());
			return transferenciaService.registraTransferencia(dto, Boolean.TRUE, appMensage.getTransferenciaSucesso());
		} catch (ClienteException e) {
			transferenciaService.registraTransferencia(dto, Boolean.FALSE, e.getMessage());
			throw e;
		}
	}

	@Transactional
	private TransferenciaDTO geraTransferencia(BigDecimal valor, Long numeroContaOrigem, Long numeroContaDestino) {
		Cliente contaOrigem = findByContaParaTransferencia(numeroContaOrigem);
		Cliente contaDestino = findByContaParaTransferencia(numeroContaDestino);
		return new TransferenciaDTO(valor, contaOrigem, contaDestino);

	}

}