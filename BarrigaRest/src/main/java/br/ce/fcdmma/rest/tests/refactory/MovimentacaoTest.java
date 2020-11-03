package br.ce.fcdmma.rest.tests.refactory;

import static br.ce.fcdmma.rest.utils.DataUtils.getDataDiferencaDias;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;
import br.ce.fcdmma.rest.tests.Movimentacao;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest{

	@BeforeClass
	public static void login() {
		Map<String, String> credenciais = new HashMap<String, String>();
		credenciais.put("email", "chaves@seubarriga.com");
		credenciais.put("senha", "pwd123");

		String TOKEN = given().body(credenciais).when().post("/signin").then().statusCode(200).extract().path("token");

		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		
		RestAssured.get("/reset").then().statusCode(200);
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao movimentacao = getMovimentacaoValida();
		
		given()
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
		movimentacao.setData_transacao(getDataDiferencaDias(2));
		
		given()
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
		Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
			.body("table", is("transacoes"))
			.body("severity", is("ERROR"))
		;
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		Integer MOVIMENTACAO_ID = getIdMovPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", MOVIMENTACAO_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	public Integer getIdMovPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
		movimentacao.setDescricao("Descrição movimentação");
		movimentacao.setEnvolvido("Envolvido na movimentação");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao(getDataDiferencaDias(-1));
		movimentacao.setData_pagamento(getDataDiferencaDias(5));
		movimentacao.setValor(100f);
		movimentacao.setStatus(true);
		
		return movimentacao;
	}
}
