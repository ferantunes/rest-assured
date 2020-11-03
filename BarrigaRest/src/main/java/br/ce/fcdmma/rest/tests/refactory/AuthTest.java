package br.ce.fcdmma.rest.tests.refactory;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest{
	
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
}
