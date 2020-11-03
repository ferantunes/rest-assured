package br.ce.fcdmma.rest.tests.refactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest{

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
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
		;
	}
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
}
