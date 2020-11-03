package br.ce.fcdmma.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;

public class BarrigaTest extends BaseTest {

	private String TOKEN;

	@Before
	public void login() {
		Map<String, String> credenciais = new HashMap<String, String>();
		credenciais.put("email", "chaves@seubarriga.com");
		credenciais.put("senha", "pwd123");
		
		TOKEN = given()
					.body(credenciais)
				.when()
					.post("/signin")
				.then()
					.statusCode(200)
					.extract().path("token");
	}

	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
		.statusCode(401);
	}

	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"Conta Teste\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201);

	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"Conta Teste Alterada\"}")
		.when()
			.put("/contas/312543")
		.then()
			.statusCode(200)
			.body("id", is(312543))
			.body("nome", is("Conta Teste Alterada"))
		;
	}
	
	@Test
	public void naoDeveInserirContaComMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"Conta Teste Alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao movimentacao = getMovimentacaoValida();
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"))
		;
	}
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao movimentacao = getMovimentacaoValida();
		movimentacao.setData_transacao("20/05/2050");
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/contas/312543")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
			.body("table", is("transacoes"))
			.body("severity", is("ERROR"))
		;
	}
	
	@Test
	public void deveCalcularSaldoContas() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id = 312543}.saldo", is("200.00"))
		;
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/transacoes/283416")
		.then()
			.statusCode(204)
		;
	}

	private Movimentacao getMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(312543);
		movimentacao.setDescricao("Descrição movimentação");
		movimentacao.setEnvolvido("Envolvido na movimentação");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao("01/01/2019");
		movimentacao.setData_pagamento("10/05/2019");
		movimentacao.setValor(100f);
		movimentacao.setStatus(true);
		
		return movimentacao;
	}
}
