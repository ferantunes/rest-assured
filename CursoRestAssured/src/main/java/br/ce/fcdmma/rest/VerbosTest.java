package br.ce.fcdmma.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class VerbosTest {
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
	}
	
	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{\"name\": \"Maribel\", \"age\": \"13\"}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Maribel"))
			.body("age", is(13))
		;
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{\"age\": \"13\"}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Pantufa</name><age>5</age></user>")
		.when()
			.post("/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Pantufa"))
			.body("user.age", is("5"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioViaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 45);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("45"))
		;
	}
	
	@Test
	public void deveDeserializarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 45);
		
		User usuarioInserido = given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		assertThat(usuarioInserido.getId(), notNullValue());
		assertThat(usuarioInserido.getName(), is("Usuario XML"));
		assertThat(usuarioInserido.getAge(), is(45));
		assertThat(usuarioInserido.getSalary(), nullValue());
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Fulano Alterado\", \"age\": \"80\"}")
		.when()
			.put("/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Fulano Alterado"))
			.body("age", is("80"))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveCustomizarURL() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Fulano Alterado\", \"age\": \"80\"}")
		.when()
			.put("/{entidade}/{userId}", "users", "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Fulano Alterado"))
			.body("age", is("80"))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveCustomizarURLParte2() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Fulano Alterado\", \"age\": \"80\"}")
			.pathParam("entidade", "users")
			.pathParam("userId", "1")
		.when()
			.put("/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Fulano Alterado"))
			.body("age", is("80"))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void naoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("/users/100000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Usuário via MAP");
		map.put("age", 21);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(map)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via MAP"))
			.body("age", is(21))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		User user = new User("Usuário via objeto", 34);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via objeto"))
			.body("age", is(34))
		;
	}
	
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		User user = new User("Usuário deserializado", 34);
		
		User usuarioInserido = given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		assertThat(usuarioInserido.getId(), notNullValue());
		assertEquals("Usuário deserializado", usuarioInserido.getName());
		assertThat(usuarioInserido.getAge(), is(34));
	}
}