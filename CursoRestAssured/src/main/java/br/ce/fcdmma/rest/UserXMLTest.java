package br.ce.fcdmma.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class UserXMLTest {
	
	@Test
	public void devoTrabalharComXML() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)
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
			.get("http://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
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
}
