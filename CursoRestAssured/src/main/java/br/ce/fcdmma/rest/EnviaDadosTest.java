package br.ce.fcdmma.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import io.restassured.http.ContentType;

public class EnviaDadosTest {
	
	@Test
	public void deveEnviarValorViaQuery() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/v2/users?format=json")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
		;
	}
	
	@Test
	public void deveEnviarValorViaQueryViaParam() {
		given()
			.log().all()
			.queryParam("format", "xml")
			.queryParam("outra", "coisa")
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
			.contentType(containsString("utf-8"))
		;
	}
	
	@Test
	public void deveEnviarValorViaHeader() {
		given()
			.log().all()
			.accept(ContentType.XML)
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
		;
	}
}
