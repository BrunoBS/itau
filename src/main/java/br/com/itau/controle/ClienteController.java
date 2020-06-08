package br.com.itau.controle;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.config.ClienteException;
import br.com.itau.config.ClienteNotFoundException;
import br.com.itau.config.InternalExecption;
import br.com.itau.config.LockException;
import br.com.itau.modelo.Cliente;
import br.com.itau.modelo.Transferencia;
import br.com.itau.servico.ClienteService;
import br.com.itau.servico.TransferenciaService;
import br.com.itau.util.AppMensage;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("v1/clientes")
@Validated
public class ClienteController {
	@Autowired
	AppMensage appMensage;

	@Autowired
	ClienteService clienteService;
	@Autowired
	TransferenciaService transferenciaService;

	@ApiOperation(value = "Endpoint para cadastrar um cliente")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Cliente Cadastrado com sucesso"),
			@ApiResponse(code = 400, message = "Bad Request: Requisição possui campos inválidos") })
	@PostMapping
	public ResponseEntity<?> save(@Valid @RequestBody Cliente cliente) {
		return new ResponseEntity<>(clienteService.save(cliente), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Endpoint para listar todos os clientes cadastrados")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Cliente Cadastrado com sucesso"),
			@ApiResponse(code = 404, message = "Not Found: Não foi localizado nenhum cliente"), })

	@GetMapping
	public ResponseEntity<?> listar() {
		return new ResponseEntity<>(clienteService.findAll(), HttpStatus.OK);
	}

	@ApiOperation(value = "Endpoint para buscar um cliente pelo número da conta")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Cliente Localizado com sucesso"),
			@ApiResponse(code = 400, message = "Bad Request: Requisição possui campos inválidos ou não possui registro com o parametro informado"), })
	@GetMapping(value = "/{numeroConta}")
	public ResponseEntity<?> findByConta(
			@PathVariable(name = "numeroConta", required = true) @NotNull Long numeroConta) {
		return new ResponseEntity<>(clienteService.findByConta(numeroConta), HttpStatus.OK);

	}

	@ApiOperation(value = "Endpoint para realizar transferência entre 2 contas. A conta origem "
			+ "	 precisa ter saldo o suficiente para a realização da transferência e a "
			+ "	 transferência deve ser de no máximo R$ 1000,00 reais")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Transferência efetuada com Sucesso"),
			@ApiResponse(code = 400, message = "Bad Request: Requisição possui campos inválidos"),
			@ApiResponse(code = 404, message = "Not Found: Não foi localizado nenhum cliente"),
			@ApiResponse(code = 409, message = "Conflict:A solicitação não pode ser executada graças a uma solicitação conflitante"), })

	@PostMapping(value = "/{numeroConta}/transferencia")
	public ResponseEntity<?> transfere(@RequestParam(name = "valor", required = true) BigDecimal valor,
			@PathVariable(name = "numeroConta", required = true) Long numeroConta,
			@RequestParam(name = "numeroContaDestino", required = true) Long numeroContaDestino) {
		Transferencia transferencia = null;
		try {
			transferencia = clienteService.transfere(valor, numeroConta, numeroContaDestino);
		} catch (RuntimeException e) {

			if (e.getCause() instanceof StaleObjectStateException) {
				throw new LockException(appMensage.getRecursoAtualizado());
			}
			if (e instanceof ClienteException || e instanceof ClienteNotFoundException) {
				throw e;
			}
			throw new InternalExecption(appMensage.getInternalServerError());

		}

		return new ResponseEntity<>(transferencia, HttpStatus.OK);
	} 

	@ApiOperation(value = "Endpoint para buscar as transferências relacionadas à uma conta, por ordem "
			+ "de data decrescente. Lembre-se que transferências sem sucesso também devem armazenadas")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Busca de transferências relacionadas à conta bancária realizada com sucesso"),
			@ApiResponse(code = 404, message = "Not Found: Não foi localizado nenhuma transferência relacionda a conta / Conta Corrente Enexiste") })
	@GetMapping(value = "/{numeroConta}/transferencia")
	public ResponseEntity<?> transferencia(@PathVariable(name = "numeroConta", required = true) Long numeroConta) {
		return new ResponseEntity<>(transferenciaService.extrato(numeroConta), HttpStatus.OK);
	}

}