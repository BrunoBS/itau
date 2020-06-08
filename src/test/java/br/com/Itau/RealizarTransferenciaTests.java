package br.com.Itau;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import br.com.itau.config.ErrorResponse;
import br.com.itau.modelo.Cliente;
import io.restassured.path.json.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
@AutoConfigureTestDatabase(replace = Replace.ANY)
class RealizarTransferenciaTests extends IntegracaoEndPointsTests {

	private Long contaInvalida = 1l;

	@BeforeAll
	public void init() throws Exception {
		postUsuario(nomeC1, new BigDecimal(1000), HttpStatus.CREATED);
		postUsuario(nomeC2, new BigDecimal(1000), HttpStatus.CREATED);

	}

	@Test
	@Order(1)
	public void testaTransferenciaComContasInvalidas() {
		JsonPath resposta = postTranasferencia(contaInvalida, new BigDecimal(1000), numC2, HttpStatus.NOT_FOUND);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getNotFound(), errorResponse.getMessage());
		assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getNenhumClientePelaConta(), "numeroConta"));

		resposta = postTranasferencia(numC1, new BigDecimal(1000), contaInvalida, HttpStatus.NOT_FOUND);
		errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getNotFound(), errorResponse.getMessage());
		assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l,
				verficaCampoComErro(errorResponse, appMensage.getNenhumClientePelaConta(), "numeroContaDestino"));
	}

	@Test
	@Order(2)
	public void testaTransferenciaEntreContasComValorZerado() {

		JsonPath resposta = postTranasferencia(numC1, BigDecimal.ZERO, numC2, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getBadRequest(), errorResponse.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getValorInvalido(), "valor"));

	}

	@Test
	@Order(3)
	public void testaTransferenciaEntreContasComValorNulo() {
		JsonPath resposta = postTranasferencia(numC1, null, numC2, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getBadRequest(), errorResponse.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getValorInvalido(), "valor"));

	}

	@Test
	@Order(4)
	public void testaTransferenciaEntreMesmaConta() {
		JsonPath resposta = postTranasferencia(numC1, new BigDecimal(150), numC1, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getBadRequest(), errorResponse.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getContaIgual(), "Numero da Conta"));

	}

	@Test
	@Order(5)
	public void testaTransferenciaEntreContasComValorMaiorQueMil() {
		JsonPath resposta = postTranasferencia(numC1, new BigDecimal(1100), numC2, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getBadRequest(), errorResponse.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getValorMaximoTransferencia(), "valor"));

	}

	@Test
	@Order(6)
	public void testaTransferenciaEntreContasComSaldoInsuficiente() {
		postTranasferencia(numC1, new BigDecimal(500), numC2, HttpStatus.OK);
		JsonPath resposta = postTranasferencia(numC1, new BigDecimal(600), numC2, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getBadRequest(), errorResponse.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getSaldoInsuficiente(), "valor"));

	}

	@Test
	@Order(7)
	public void testaTransferenciaEntreContas() {
		validaTransferencia(numC2, numC1, new BigDecimal(500));
		validaTransferencia(numC2, numC1, new BigDecimal(100));
		validaTransferencia(numC2, numC1, new BigDecimal(50));
		validaTransferencia(numC2, numC1, new BigDecimal(350));

		validaTransferencia(numC1, numC2, new BigDecimal(500));

		Cliente conta1 = buscaCliente(numC1);
		Cliente conta2 = buscaCliente(numC2);
		assertEquals(new BigDecimal(1000).setScale(2), conta1.getSaldo().setScale(2));
		assertEquals(new BigDecimal(1000).setScale(2), conta2.getSaldo().setScale(2));
	}

	private void validaTransferencia(Long contaOrigem, Long contaDestino, BigDecimal valor) {
		Cliente origem = buscaCliente(contaOrigem);
		Cliente destino = buscaCliente(contaDestino);
		BigDecimal origemEsperada = origem.getSaldo().subtract(valor);
		BigDecimal destinoEsperado = destino.getSaldo().add(valor);

		postTranasferencia(origem.getNumeroConta(), valor, destino.getNumeroConta(), HttpStatus.OK);
		Cliente novaOrigem = buscaCliente(contaOrigem);
		Cliente novodestino = buscaCliente(contaDestino);
		assertEquals(origemEsperada.setScale(2), novaOrigem.getSaldo().setScale(2));
		assertEquals(destinoEsperado.setScale(2), novodestino.getSaldo().setScale(2));
	}

	private Cliente buscaCliente(final Long conta) {
		JsonPath resposta = getClientePelaConta(conta, HttpStatus.OK);
		Cliente c = resposta.getObject("", Cliente.class);
		return c;
	}

}
