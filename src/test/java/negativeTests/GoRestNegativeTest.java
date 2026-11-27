package negativeTests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import pojo.GoRestUser;
import utils.JsonDataReader;
import utils.TestDataUtil;

/**
 * Negative cases for GoRest: authentication, validation (data-driven) and unknown resources.
 */
public class GoRestNegativeTest {

	private Integer createdUserId;

	// ------------------------------------------------------------------
	// Authentication - 401
	// ------------------------------------------------------------------

	@Test(groups = "negative")
	public void createUser_withoutToken_returns401()
	{
		given().spec(RestClient.gorestWithoutAuth())
		  .body(TestDataUtil.randomGoRestUser())
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.json(401))
		            .body("message", equalTo("Authentication failed"));
	}

	@Test(groups = "negative")
	public void createUser_withInvalidToken_returns401()
	{
		given().spec(RestClient.gorestWithoutAuth())
		  .header("Authorization", "Bearer invalid-token-123")
		  .body(TestDataUtil.randomGoRestUser())
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.json(401))
		            .body("message", equalTo("Invalid token"));
	}

	// ------------------------------------------------------------------
	// Validation - 422 (data-driven from testdata/gorest-invalid-users.json)
	// ------------------------------------------------------------------

	@DataProvider(name = "invalidUsers")
	public Object[][] invalidUsers()
	{
		return JsonDataReader.toDataProvider("testdata/gorest-invalid-users.json");
	}

	@Test(groups = "negative", dataProvider = "invalidUsers")
	public void createUser_withInvalidField_returns422(Map<String, Object> row)
	{
		String email = "RANDOM".equals(row.get("email")) ? TestDataUtil.randomEmail() : (String) row.get("email");

		GoRestUser user = GoRestUser.builder()
				.name((String) row.get("name"))
				.email(email)
				.gender((String) row.get("gender"))
				.status((String) row.get("status"))
				.build();

		given().spec(RestClient.gorest())
		  .body(user)
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.json(422))
		            .body("field", hasItem(row.get("field")))
		            .body("find { it.field == '" + row.get("field") + "' }.message", equalTo(row.get("message")));
	}

	@Test(groups = "negative")
	public void createUser_withEmptyBody_returns422ForAllRequiredFields()
	{
		given().spec(RestClient.gorest())
		  .body("{}")
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.json(422))
		            .body("field", hasItems("email", "name", "gender", "status"));
	}

	@Test(groups = "negative")
	public void createUser_withDuplicateEmail_returns422()
	{
		GoRestUser user = TestDataUtil.randomGoRestUser();

		createdUserId = given().spec(RestClient.gorest())
		  .body(user)
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.created())
		            .extract()
		              .path("id");

		// same email again
		user.setName("Another Name");
		given().spec(RestClient.gorest())
		  .body(user)
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.json(422))
		            .body("find { it.field == 'email' }.message", equalTo("has already been taken"));
	}

	// ------------------------------------------------------------------
	// Unknown resource - 404
	// ------------------------------------------------------------------

	@Test(groups = "negative")
	public void getUser_withUnknownId_returns404()
	{
		given().spec(RestClient.gorest())
		  .pathParam("userId", 999999999)
		    .when()
		      .get(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.json(404))
		            .body("message", equalTo("Resource not found"));
	}

	@Test(groups = "negative")
	public void updateUser_withUnknownId_returns404()
	{
		given().spec(RestClient.gorest())
		  .pathParam("userId", 999999999)
		  .body(Map.of("name", "Nobody"))
		    .when()
		      .put(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.json(404))
		            .body("message", equalTo("Resource not found"));
	}

	@Test(groups = "negative")
	public void deleteUser_withUnknownId_returns404()
	{
		given().spec(RestClient.gorest())
		  .pathParam("userId", 999999999)
		    .when()
		      .delete(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.json(404))
		            .body("message", equalTo("Resource not found"));
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp()
	{
		TestDataCleaner.deleteGoRestUser(createdUserId);
		createdUserId = null;
	}
}
