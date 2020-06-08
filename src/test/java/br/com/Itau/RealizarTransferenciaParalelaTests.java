package br.com.Itau;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
@AutoConfigureTestDatabase(replace = Replace.ANY)
class RealizarTransferenciaParalelaTests extends IntegracaoEndPointsTests {

	@Test
	@Order(1)
	public void testaTransferenciaEntreContasParalelo() {
		postUsuario(nomeC1, new BigDecimal(1000), HttpStatus.CREATED);
		postUsuario(nomeC2, new BigDecimal(1000), HttpStatus.CREATED);

		IntStream.range(0, 10).parallel().forEach(nbr -> {
			JsonPath resposta = postTransferencia(numC1, new BigDecimal(100), numC2);
			String s = resposta.getString("");
			if (s.contains("operacaoType")) {
				Transferencia t = resposta.getObject("", Transferencia.class);
				assertEquals(OperacaoType.DEBITO, t.getOperacaoType());
				assertEquals("Débito", t.getOperacaoType().toString());
				assertEquals(numC1, t.getContaOrigem());
				assertEquals(numC2, t.getContaDestino());
			
			} else {
				ErrorResponse errorResponse = resposta.getObject("", ErrorResponse.class);
				assertEquals(appMensage.getConflict(), errorResponse.getMessage());
				assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getCode());
				assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), errorResponse.getStatus());
				assertEquals(1l, verficaCampoComErro(errorResponse, appMensage.getRecursoAtualizado(), ""));
			}
		});
	}

	protected JsonPath postTransferencia(Long conta, BigDecimal valor, Long contaDestino) {
		return given().pathParam("numeroConta", conta).queryParam("numeroContaDestino", contaDestino)
				.queryParam("valor", valor).contentType(ContentType.JSON).when()
				.post(getURL("/clientes/{numeroConta}/transferencia")).andReturn().jsonPath();
	}

}
