package createUserPOJO;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import constants.SchemaPaths;
import utils.FileUtil;
import utils.TestDataUtil;

public class CreateUserAPI {

	private Integer Id;

	@Test(groups = "smoke")
	public void createUser()
	{
		// Load the payload from json/user.json and replace the email with a unique one
		// (a static email fails with 422 "has already been taken" on the second run).
		JSONObject payload = new JSONObject(FileUtil.readAsString("json/user.json"));
		payload.put("email", TestDataUtil.randomEmail());

		Id =given().spec(RestClient.gorest())
		    .body(payload.toString())
		     .post(Routes.GOREST_USERS)
		       .then()
		         .spec(ResponseSpecs.created())
		             .body(matchesJsonSchemaInClasspath(SchemaPaths.GOREST_USER))
		             .body("email", equalTo(payload.getString("email")))
		             .extract()
		               .path("id");

		Assert.assertNotNull(Id);
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(Id);
	}

}
