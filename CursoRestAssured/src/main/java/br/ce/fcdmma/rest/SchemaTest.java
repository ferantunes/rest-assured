package br.ce.fcdmma.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import io.restassured.RestAssured;

public class SchemaTest {
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
	}
	
	@Test
	public void deveValidarSchemaXML() {
		given()
			.log().all()
		.when()
			.get("/usersXML")
		.then()
			.log().all()
			.statusCode(200)
			.body(matchesXsdInClasspath("users.xsd"));
		;
	}
	
	@Test(expected = SAXParseException.class)
	public void naoDeveValidarSchemaXMLInvalido() {
		given()
			.log().all()
		.when()
			.get("/invalidUsersXML")
		.then()
			.log().all()
			.statusCode(200)
			.body(matchesXsdInClasspath("users.xsd"));
		;
	}
	
	@Test
	public void deveValidarSchemaJson() {
		given()
			.log().all()
		.when()
			.get("/users")
		.then()
			.log().all()
			.statusCode(200)
			.body(matchesJsonSchemaInClasspath("users.json"));
		;
	}
}
