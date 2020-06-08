package br.com.Itau;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

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
class SalvarClienteTests extends IntegracaoEndPointsTests {

	@Test
	@Order(1)
	public void testaCadastrarCliente() {
		JsonPath postBruno = postUsuario("Bruno Barbosa", new BigDecimal(1000), HttpStatus.CREATED);
		JsonPath postSilvio = postUsuario("Silvio Barbosa", new BigDecimal(1000), HttpStatus.CREATED);

		Cliente respostaBruno = postBruno.getObject("", Cliente.class);
		assertEquals(1, respostaBruno.getId());
		assertEquals(1000L, respostaBruno.getNumeroConta());

		Cliente respostaSilvio = postSilvio.getObject("", Cliente.class);
		assertEquals(2, respostaSilvio.getId());
		assertEquals(1001L, respostaSilvio.getNumeroConta());
	}

	@Test
	@Order(2)
	public void testaErroAoCadastrarCliente() {
		JsonPath user1 = postUsuario(null, new BigDecimal(1000), HttpStatus.BAD_REQUEST);
		JsonPath user2 = postUsuario("Silvio Barbosa", null, HttpStatus.BAD_REQUEST);
		ErrorResponse errorResponse1 = user1.getObject("", ErrorResponse.class);
		ErrorResponse errorResponse2 = user2.getObject("", ErrorResponse.class);
		assertEquals(1l, verficaCampoComErro(errorResponse1, "não pode estar em branco", "nome"));
		assertEquals(1l, verficaCampoComErro(errorResponse2, "não pode ser nulo", "saldo"));

	}



}
