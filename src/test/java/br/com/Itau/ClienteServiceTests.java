package br.com.Itau;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import br.com.itau.config.ClienteException;
import br.com.itau.config.ClienteNotFoundException;
import br.com.itau.modelo.Cliente;
import br.com.itau.modelo.Transferencia;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
@AutoConfigureTestDatabase(replace = Replace.ANY)
class ClienteServiceTests extends MockTests {

	@Test
	public void listarTodosClientesSemTerClientesCadastradosTest() {
		configFindAllMock(new ArrayList<Cliente>());
		Exception exception = Assertions.assertThrows(ClienteNotFoundException.class, () -> {
			clienteService.findAll();
		});
		assertEquals(exception.getMessage(), appMensage.getNenhumRegistroLocalizado());
	}

	@Test
	public void listarTodosClientesComClientesCadastradosTest() {
		configFindAllMock();
		List<Cliente> clientes = clienteService.findAll();
		assertEquals(1l, clientes.get(0).getId());
		assertEquals(nomeC1, clientes.get(0).getNome());
		assertEquals(numC1, clientes.get(0).getNumeroConta());
		assertEquals(2l, clientes.get(1).getId());
		assertEquals(nomeC2, clientes.get(1).getNome());
		assertEquals(numC2, clientes.get(1).getNumeroConta());
	}

	@Test
	public void listarClienteSemCadastro() {
		configFindMock(Optional.ofNullable(null), numC1);
		Exception exception = Assertions.assertThrows(ClienteException.class, () -> {
			clienteService.findByConta(numC1);
		});
		assertEquals(exception.getMessage(), appMensage.getNenhumRegistroLocalizado());

	}

	@Test
	public void listarClienteComCadastro() {
		configFindMock(Optional.of(getCliente(1l, nomeC1, numC1)), numC1);

		Cliente cliente = clienteService.findByConta(numC1);
		assertEquals(1l, cliente.getId());
		assertEquals(nomeC1, cliente.getNome());
		assertEquals(numC1, cliente.getNumeroConta());

		configFindMock(Optional.of(getCliente(2l, nomeC2, numC2)), numC2);
		cliente = clienteService.findByConta(numC2);
		assertEquals(2l, cliente.getId());
		assertEquals(nomeC2, cliente.getNome());
		assertEquals(numC2, cliente.getNumeroConta());
	}

	@Test
	public void realizaTransferenciaComSucesso() {
		Cliente clienteOrigem = getCliente(1l, nomeC1, numC1);
		Cliente clienteDestino = getCliente(2l, nomeC2, numC2);
		configTrasferenciaMock(clienteOrigem, clienteDestino, new BigDecimal(100), Boolean.TRUE);
		Transferencia transfere = clienteService.transfere(new BigDecimal(100), numC1, numC2);
		assertTrue(transfere.getSucesso());
		assertEquals(numC1, transfere.getContaOrigem());
		assertEquals(numC2, transfere.getContaDestino());
		assertEquals(new BigDecimal(100).setScale(2), transfere.getValor().setScale(2));
	}

	@Test
	public void realizaTransferenciaComValorNulo() {
		transferencia(numC1, numC2, null, appMensage.getValorInvalido());
	}

	@Test
	public void realizaTransferenciaComValorZero() {
		transferencia(numC1, numC2, BigDecimal.ZERO, appMensage.getValorInvalido());

	}

	@Test
	public void realizaTransferenciaComValorMaiorQueMil() {
		transferencia(numC1, numC2, new BigDecimal(1500l),
				appMensage.getValorMaximoTransferencia());
	}

	@Test
	public void realizaTransferenciaParaAMesmaOrigem() {

		Cliente clienteOrigem = getCliente(1l, nomeC1, numC1);
		configTrasferenciaMock(clienteOrigem, clienteOrigem, new BigDecimal(50l), Boolean.TRUE);
		Exception exception = Assertions.assertThrows(ClienteException.class, () -> {
			clienteService.transfere(new BigDecimal(50l), numC1, numC1);
		});
		assertEquals(exception.getMessage(), appMensage.getContaIgual());

	}

	@Test
	public void realizaTransferenciaComValorMaiorQueOSaldo() {
		transferencia(numC1, numC2, new BigDecimal(950l), appMensage.getSaldoInsuficiente());
	}

	private void transferencia(Long origem, Long destino, BigDecimal valor, String menssagemEsperada) {
		Cliente clienteOrigem = getCliente(1l, nomeC1, origem);
		Cliente clienteDestino = getCliente(2l, nomeC2, destino);
		configTrasferenciaMock(clienteOrigem, clienteDestino, valor, Boolean.TRUE);
		Exception exception = Assertions.assertThrows(ClienteException.class, () -> {
			clienteService.transfere(valor, origem, destino);
		});
		assertEquals(exception.getMessage(), menssagemEsperada);
	}

}
