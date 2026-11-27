package getAPITest;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import constants.Routes;
import constants.SchemaPaths;
public class ProductAPIs {

	@Test(groups = "smoke")
	public void productGetTest_1()
	{
		//URL = "https://fakestoreapi.com/products"
		given().spec(RestClient.fakeStore())
		   .get(Routes.FAKESTORE_PRODUCTS)
		       .then()
		          .spec(ResponseSpecs.ok());
	}

	@Test
	public void productGetTest_2()
	{
		//URL = "https://fakestoreapi.com/products"
		given().spec(RestClient.fakeStore())
		  .when()
		     .get(Routes.FAKESTORE_PRODUCTS)
		        .then()
		           .spec(ResponseSpecs.ok())
		                   .and()
		                      .statusLine("HTTP/1.1 200 OK")
		                           .body("size()", greaterThan(0))
		                           .body(matchesJsonSchemaInClasspath(SchemaPaths.FAKESTORE_PRODUCT_LIST));
	}

	@Test
	public void productGetById()
	{
		given().spec(RestClient.fakeStore())
		  .pathParam("productId", 1)
		    .when()
		      .get(Routes.FAKESTORE_PRODUCT_BY_ID)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .body("id", equalTo(1))
		            .body(matchesJsonSchemaInClasspath(SchemaPaths.FAKESTORE_PRODUCT));
	}

}
