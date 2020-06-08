package br.com.Itau;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import br.com.itau.config.ErrorResponse;
import br.com.itau.modelo.OperacaoType;
import br.com.itau.modelo.Transferencia;
import io.restassured.path.json.JsonPath;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
@AutoConfigureTestDatabase(replace = Replace.ANY)
class ListarTransferenciaTests extends IntegracaoEndPointsTests {



	
	@Test
	@Order(1)
	public void testaListarTransferenciaRelacionadaAUmaContaSemTransferencia() {
		JsonPath resposta = getTransferencia(numC1, HttpStatus.NOT_FOUND);
		ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
		assertEquals(appMensage.getNotFound(), errorResponse.getMessage());
		assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getStatus());
		assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getNenhumRegistroLocalizado(), ""));
	}

	@Test
	@Order(2)
	public void testaListarTransferenciaRelacionadaAUmaContaComTransferenciaComSucesso() {

		postUsuario("Bruno Barbosa", new BigDecimal(1000), HttpStatus.CREATED);
		postUsuario("Silvio Barbosa", new BigDecimal(1000), HttpStatus.CREATED);

		postTranasferencia(numC1, new BigDecimal(500), numC2, HttpStatus.OK);
		JsonPath resposta = getTransferencia(numC1, HttpStatus.OK);
		List<Transferencia> lista = resposta.getList("", Transferencia.class);
		Transferencia t = lista.get(0);
		assertEquals(OperacaoType.DEBITO, t.getOperacaoType());
		assertEquals("Débito", t.getOperacaoType().toString());
		assertEquals(new BigDecimal(500).setScale(2), t.getValor().setScale(2));
		assertEquals(numC1, t.getContaOrigem());
		assertEquals(numC2, t.getContaDestino());
		assertTrue(t.getSucesso());
	}

}
