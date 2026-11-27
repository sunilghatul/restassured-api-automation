package mylombok;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import pojo.GoRestUser;
import utils.TestDataUtil;

public class MyuserTest {

	private Integer Id;

	@Test
	public void cCreateAndUpdateUser_WithPOJO()
	{
		// Lombok @Builder
		GoRestUser myUserLombok = GoRestUser.builder()
		          .name("Sanjay")
		          .email(TestDataUtil.randomEmail())
		          .gender("male")
		          .status("active")
		          .build();

	   Id = given().spec(RestClient.gorest())
		    .body(myUserLombok)
		       .when()
		         .post(Routes.GOREST_USERS)
		           .then()
		             .spec(ResponseSpecs.created())
		             .extract()
		               .path("id");

	   // Lombok @Data setters
	   myUserLombok.setName("Rohit");
	   myUserLombok.setGender("male");

	   given().spec(RestClient.gorest())
		     .body(myUserLombok)
		     .pathParam("userId", Id)
		       .when()
		         .put(Routes.GOREST_USER_BY_ID)
		           .then()
		             .spec(ResponseSpecs.ok())
	                     .and()
	                       .body("name", equalTo(myUserLombok.getName()))
	                       .body("gender", equalTo(myUserLombok.getGender()));
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(Id);
	}
}
