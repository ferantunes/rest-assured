package br.ce.fcdmma.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserXMLTest {
	
	public static RequestSpecification reqSpec;
	public static ResponseSpecification resSpec;
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
		
		RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
		requestSpecBuilder.log(LogDetail.ALL);
		reqSpec = requestSpecBuilder.build();
		
		ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
		responseSpecBuilder.expectStatusCode(200);
		resSpec = responseSpecBuilder.build();
		
		RestAssured.requestSpecification = reqSpec;
		RestAssured.responseSpecification = resSpec;
	}
	
	@Test
	public void devoTrabalharComXML() {
		given()
			.log().all()
		.when()
			.get("/usersXML/3")
		.then()
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos")
			.body("name", hasItem("Zezinho"))
			.body("name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void devoFazerPesquisasAvancadasComXML() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.rootPath("users.user")
			.body("size()", is(3))
			.body("findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("@id", hasItems("1", "2", "3"))
			.body("find{it.age == 25}.name", is("Maria Joaquina"))
			.body("findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("salary.find{it.salary != null}.toDouble()", is(1234.5678d))
			.body("age.collect{it.toInteger() * 2}", hasItems(40,50,60))
			.body("name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}
	
	@Test
	public void devoFazerPesquisasAvancadasComXMLEJava() {
		ArrayList<NodeImpl> nomes = given()
		.when()
			.get("/usersXML")
		.then()
			.extract().path("users.user.name.findAll{it.toString().contains('n')}");
		
		assertEquals(2, nomes.size());
		assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		assertTrue("Ana JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	@Test
	public void devoFazerPesquisasAvancadasComXPath() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.body(hasXPath("count(/users/user)", is("3")))
			.body(hasXPath("/users/user[@id = '1']"))
			.body(hasXPath("//user[@id = '2']"))
			.body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
			.body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Luizinho"), containsString("Zezinho"))))
			.body(hasXPath("/users/user/name", is("João da Silva")))
			.body(hasXPath("//name", is("João da Silva")))
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(.,'n')])", is("2")))
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
		;
	}
}
