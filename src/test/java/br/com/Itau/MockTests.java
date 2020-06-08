package br.com.Itau;

import static org.mockito.ArgumentMatchers.anyLong;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.itau.dto.TransferenciaDTO;
import br.com.itau.modelo.Cliente;
import br.com.itau.modelo.OperacaoType;
import br.com.itau.modelo.Transferencia;
import br.com.itau.repositorio.ClienteRepository;
import br.com.itau.servico.ClienteService;
import br.com.itau.servico.TransferenciaService;
import br.com.itau.util.AppMensage;

public abstract class MockTests extends AppTestes {

	@Autowired
	protected AppMensage appMensage;

	@InjectMocks
	protected ClienteService clienteService;

	@Mock
	protected AppMensage appMensageMock;

	@Mock
	protected ClienteRepository clienteRepository;

	@Mock
	private TransferenciaService transferenciaService;

	@BeforeAll
	public void init() throws Exception {
		BDDMockito.when(appMensageMock.getNenhumRegistroLocalizado())
				.thenReturn(appMensage.getNenhumRegistroLocalizado());
		BDDMockito.when(appMensageMock.getNenhumClientePelaConta()).thenReturn(appMensage.getNenhumClientePelaConta());
		BDDMockito.when(appMensageMock.getSaldoInsuficiente()).thenReturn(appMensage.getSaldoInsuficiente());
		BDDMockito.when(appMensageMock.getContaIgual()).thenReturn(appMensage.getContaIgual());
		BDDMockito.when(appMensageMock.getTransferenciaSucesso()).thenReturn(appMensage.getTransferenciaSucesso());
		BDDMockito.when(appMensageMock.getValorMaximoTransferencia())
				.thenReturn(appMensage.getValorMaximoTransferencia());
		BDDMockito.when(appMensageMock.getValorInvalido()).thenReturn(appMensage.getValorInvalido());
	}

	protected Cliente getCliente(Long id, String nome, Long numeroConta) {
		Cliente c = new Cliente();
		c.setId(id);
		c.setSaldo(new BigDecimal(800l));
		c.setNumeroConta(numeroConta);
		c.setNome(nome);
		return c;
	}

	protected void configFindAllMock(List<Cliente> clientes) {
		BDDMockito.when(clienteRepository.findAll()).thenReturn(clientes);
	}

	protected void configFindAllMock() {
		configFindAllMock(Arrays.asList(getCliente(1l, nomeC1, 1000l), getCliente(2l, nomeC2, 1001l)));
	}

	protected void configFindMock(Optional<Cliente> cliente, Long conta) {
		BDDMockito.when(clienteRepository.findByNumeroConta(BDDMockito.eq(conta))).thenReturn(cliente);
		BDDMockito.when(clienteRepository.findByNumeroContaOrderByIdDesc(BDDMockito.eq(conta))).thenReturn(cliente);
	}

	@SuppressWarnings("unchecked")
	protected void configTrasferenciaMock(Cliente clienteOrigem, Cliente clienteDestino, BigDecimal valor,
			Boolean sucesso) {

		Transferencia t = Transferencia.builder().contaDestino(clienteDestino.getNumeroConta())
				.contaOrigem(clienteOrigem.getNumeroConta()).valor(valor).data(LocalDateTime.now()).sucesso(sucesso)
				.operacaoType(OperacaoType.DEBITO).build();

		BDDMockito.when(clienteRepository.findByNumeroContaOrderByIdDesc(anyLong()))
				.thenReturn(Optional.of(clienteOrigem), Optional.of(clienteDestino));
		final TransferenciaDTO dto = new TransferenciaDTO(valor, clienteOrigem, clienteDestino);

		BDDMockito.when(transferenciaService.registraTransferencia(dto, sucesso, appMensage.getTransferenciaSucesso()))
				.thenReturn(t);

	}

}
