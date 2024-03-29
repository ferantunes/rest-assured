package br.ce.fcdmma.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {

	public static void main(String[] args) {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		System.out.println("Body => " + response.body().asString());
		System.out.println("Status Code => " + response.statusCode());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}
}
