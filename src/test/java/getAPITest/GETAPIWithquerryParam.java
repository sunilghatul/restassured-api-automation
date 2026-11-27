package getAPITest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import pojo.GoRestUser;
import utils.TestDataUtil;

public class GETAPIWithquerryParam {

	private Integer createdUserId;

	@Test
	public void GetAPIwithQuerryparam()
	{
		// assert that the filter is applied - not on a result count that changes daily
		given().spec(RestClient.gorest())
		   .queryParam("status", "inactive")
		   .queryParam("per_page", 10)
		     .when()
		       .get(Routes.GOREST_USERS)
		         .then()
		           .spec(ResponseSpecs.ok())
		             .body("status", everyItem(equalTo("inactive")))
		             .body("size()", lessThanOrEqualTo(10));
	}

	@Test
	public void GetAPIwithQuerryparamWithHashMap()
	{
		Map <String, Object> querymap = new HashMap<>();
		querymap.put("gender", "female");
		querymap.put("status", "active");
		given().spec(RestClient.gorest())
		     .queryParams(querymap)
		       .when()
		       .get(Routes.GOREST_USERS)
		         .then()
		           .spec(ResponseSpecs.ok())
		             .body("gender", everyItem(equalTo("female")))
		             .body("status", everyItem(equalTo("active")));
	}

	@DataProvider
	public Object [] [] getUserData()
	{
		return new Object[][]
				{
			{"male"},
			{"female"}
				};
	}

	@Test(dataProvider="getUserData")
	public void GetAPIwithPathParameter(String gender)
	{
		// create a user of the given gender, then read it back through the {userId} path parameter
		GoRestUser user = TestDataUtil.randomGoRestUser();
		user.setGender(gender);

		createdUserId = given().spec(RestClient.gorest())
		  .body(user)
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.created())
		            .extract()
		              .path("id");

		given().spec(RestClient.gorest())
		     .pathParam("userId", createdUserId)
		       .when()
		       .get(Routes.GOREST_USER_BY_ID)
		         .then()
		           .spec(ResponseSpecs.ok())
		             .body("id", equalTo(createdUserId))
		             .body("gender", equalTo(gender));
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(createdUserId);
		createdUserId = null;
	}

}
