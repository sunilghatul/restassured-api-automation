package negativeTests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import constants.Routes;

/**
 * Negative cases for ReqRes: unknown user, login / register validation.
 */
public class ReqResNegativeTest {

	@Test(groups = "negative")
	public void getUser_withUnknownId_returns404WithEmptyBody()
	{
		given().spec(RestClient.reqres())
		  .pathParam("userId", 23)
		    .when()
		      .get(Routes.REQRES_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.json(404))
		            .body("$", anEmptyMap());
	}

	@Test(groups = "negative")
	public void login_withoutPassword_returns400()
	{
		given().spec(RestClient.reqres())
		  .body(Map.of("email", "peter@klaven"))
		    .when()
		      .post(Routes.REQRES_LOGIN)
		        .then()
		          .spec(ResponseSpecs.json(400))
		            .body("error", equalTo("Missing password"));
	}

	@Test(groups = "negative")
	public void login_withoutEmail_returns400()
	{
		given().spec(RestClient.reqres())
		  .body(Map.of("password", "cityslicka"))
		    .when()
		      .post(Routes.REQRES_LOGIN)
		        .then()
		          .spec(ResponseSpecs.json(400))
		            .body("error", equalTo("Missing email or username"));
	}

	@Test(groups = "negative")
	public void register_withUndefinedUser_returns400()
	{
		given().spec(RestClient.reqres())
		  .body(Map.of("email", "someone@nowhere.com", "password", "secret"))
		    .when()
		      .post(Routes.REQRES_REGISTER)
		        .then()
		          .spec(ResponseSpecs.json(400))
		            .body("error", equalTo("Note: Only defined users succeed registration"));
	}

	/** Positive counterpart, so the negative cases above are known to hit a working endpoint. */
	@Test(groups = "smoke")
	public void login_withValidCredentials_returnsToken()
	{
		given().spec(RestClient.reqres())
		  .body(Map.of("email", "eve.holt@reqres.in", "password", "cityslicka"))
		    .when()
		      .post(Routes.REQRES_LOGIN)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .body("token", notNullValue());
	}
}
