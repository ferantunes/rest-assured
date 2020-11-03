package br.ce.fcdmma.rest.tests.refactory;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest{

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
	public void naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
		.statusCode(401);
	}
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
}
