package br.ce.fcdmma.rest.tests.refactory;

import static br.ce.fcdmma.rest.utils.BarrigaUtils.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.fcdmma.rest.core.BaseTest;

public class SaldoTest extends BaseTest{

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
}
