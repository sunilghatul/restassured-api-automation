package updateUserPOJO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import pojo.GoRestUser;
import utils.TestDataUtil;

public class UpdateUserWithPOJO {

	//Create user: POST
	//Get User : GET
	//Update the User : PUT/PATCH
	//Delete the User : DELETE

	private Integer Id;

	@Test(groups = "smoke")
	public void createUserwith_POJO()
	{
		GoRestUser user = new GoRestUser(null, "Sunil", TestDataUtil.randomEmail(), "male", "active");

	   Id = given().spec(RestClient.gorest())
		    .body(user)
		       .when()
		         .post(Routes.GOREST_USERS)
		           .then()
		             .spec(ResponseSpecs.created())
		             .extract()
		               .path("id");

	   user.setName("sunita");
	   user.setGender("female");

	   given().spec(RestClient.gorest())
		     .body(user)
		     .pathParam("userId", Id)
		       .when()
		         .put(Routes.GOREST_USER_BY_ID)
		           .then()
		             .spec(ResponseSpecs.ok())
	                     .and()
	                       .body("name", equalTo("sunita"))
	                       .body("gender", equalTo("female"));
	}

	@Test
	public void patchUser_updatesOnlyGivenField()
	{
		GoRestUser user = TestDataUtil.randomGoRestUser();

		Id = given().spec(RestClient.gorest())
		    .body(user)
		       .when()
		         .post(Routes.GOREST_USERS)
		           .then()
		             .spec(ResponseSpecs.created())
		             .extract()
		               .path("id");

		// PATCH only the status - every other field must stay unchanged
		given().spec(RestClient.gorest())
		  .body(Map.of("status", "inactive"))
		  .pathParam("userId", Id)
		    .when()
		      .patch(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .body("status", equalTo("inactive"))
		            .body("name", equalTo(user.getName()))
		            .body("email", equalTo(user.getEmail()))
		            .body("gender", equalTo(user.getGender()));
	}

	@Test
	public void deleteUser_thenGetReturns404()
	{
		Integer userId = given().spec(RestClient.gorest())
		    .body(TestDataUtil.randomGoRestUser())
		       .when()
		         .post(Routes.GOREST_USERS)
		           .then()
		             .spec(ResponseSpecs.created())
		             .extract()
		               .path("id");

		given().spec(RestClient.gorest())
		  .pathParam("userId", userId)
		    .when()
		      .delete(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.noContent());

		given().spec(RestClient.gorest())
		  .pathParam("userId", userId)
		    .when()
		      .get(Routes.GOREST_USER_BY_ID)
		        .then()
		          .spec(ResponseSpecs.json(404))
		            .body("message", equalTo("Resource not found"));
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(Id);
		Id = null;
	}

}
