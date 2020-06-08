package br.com.Itau;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import br.com.itau.config.ErrorResponse;
import br.com.itau.modelo.Cliente;
import br.com.itau.util.AppMensage;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

public abstract class IntegracaoEndPointsTests extends AppTestes {

	@Autowired
	protected AppMensage appMensage;

	@LocalServerPort
	private int port;

	public String getURL(String recurso) {
		return "http://localhost:" + port + "/v1" + recurso;
	}

	protected JsonPath postTranasferencia(Long conta, BigDecimal valor, Long contaDestino, HttpStatus statusCode) {
		return given().pathParam("numeroConta", conta).queryParam("numeroContaDestino", contaDestino)
				.queryParam("valor", valor).contentType(ContentType.JSON).expect().statusCode(statusCode.value()).when()
				.post(getURL("/clientes/{numeroConta}/transferencia")).andReturn().jsonPath();
	}

	protected JsonPath getTransferencia(Long numeroConta, HttpStatus statusCode) {
		return given().pathParam("numeroConta", numeroConta).contentType(ContentType.JSON).expect()
				.statusCode(statusCode.value()).when().get(getURL("/clientes/{numeroConta}/transferencia")).andReturn()
				.jsonPath();
	}

	protected JsonPath getClientePelaConta(Long conta, HttpStatus statusCode) {
		return given().pathParam("numeroConta", conta).contentType(ContentType.JSON).expect()
				.statusCode(statusCode.value()).when().get(getURL("/clientes/{numeroConta}")).andReturn().jsonPath();
	}

	protected JsonPath getTodosCliente(HttpStatus statusCode) {
		return given().contentType(ContentType.JSON).expect().statusCode(statusCode.value()).when()
				.get(getURL("/clientes")).andReturn().jsonPath();
	}

	protected JsonPath postUsuario(String nome, BigDecimal saldo, HttpStatus statusCode) {
		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		cliente.setSaldo(saldo);

		return given().body(cliente).header("Accept", "application/json").contentType(ContentType.JSON).expect()
				.statusCode(statusCode.value()).when().post(getURL("/clientes")).andReturn().jsonPath();

	}

	protected long verficaCampoComErro(ErrorResponse errorResponse, final String mensagem, final String field) {
		return errorResponse.getErrors().stream()
				.filter(f -> f.getMessage().contains(mensagem) && f.getField().contains(field)).count();
	}
}