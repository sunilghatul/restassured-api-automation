package getAPITest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pojo.GoRestUser;
import utils.TestDataUtil;

public class FetchResponsedata {

	private Integer createdUserId;

	@Test
	public void GetContactAPI_withInvalidToken()
	{
		given().spec(RestClient.contactList())
		  .header("Authorization" , "Bearer ")
		       .when()
		       .get(Routes.CONTACTS)
		         .then()
		            .assertThat()
		               .statusCode(401)
		                 .and()
		                   .body("error", equalTo("Please authenticate."));
	}

	@Test
	public void GetContactAPI_withInvalidToken_Withextract()
	{
		String errMsg =given().spec(RestClient.contactList())
		  .header("Authorization" , "Bearer ")
		       .when()
		       .get(Routes.CONTACTS)
		         .then()
		                   .extract()
		                      .path("error");

		Assert.assertEquals(errMsg, "Please authenticate.");
	}

	@Test
	public void GetSingleUser()
	{
		// create our own user instead of relying on a hard-coded id that GoRest may have purged
		GoRestUser user = TestDataUtil.randomGoRestUser();
		createdUserId = given().spec(RestClient.gorest())
		  .body(user)
		    .when()
		      .post(Routes.GOREST_USERS)
		        .then()
		          .spec(ResponseSpecs.created())
		            .extract()
		              .path("id");

		Response resp =given().spec(RestClient.gorest())
		  .pathParam("userId", createdUserId)
		  .when()
		     .get(Routes.GOREST_USER_BY_ID);

		JsonPath js = resp.jsonPath();
		Assert.assertEquals(resp.statusCode(), 200);
		Assert.assertEquals(js.getInt("id"), createdUserId.intValue());
		Assert.assertEquals(js.getString("name"), user.getName());
		Assert.assertEquals(js.getString("email"), user.getEmail());
		Assert.assertEquals(js.getString("gender"), user.getGender());
	}


	@Test
	public void GetUsersData()
	{
		Response resp =given().spec(RestClient.gorest())
		  .when()
		     .get(Routes.GOREST_USERS);

	JsonPath js = resp.jsonPath();
	List<Integer> userids =js.getList("id");
	List<String> userGenders =js.getList("gender");

	Assert.assertEquals(resp.statusCode(), 200);
	Assert.assertFalse(userids.isEmpty(), "user list should not be empty");
	Assert.assertEquals(userGenders.size(), userids.size(), "every user should have a gender");
	userids.forEach(id -> Assert.assertNotNull(id));
	userGenders.forEach(g -> Assert.assertTrue(g.matches("male|female"), "unexpected gender " + g));
	}


	@Test
	public void getDetailData ()
	{
		Response resp =given().spec(RestClient.fakeStore())
		  .when()
		     .get(Routes.FAKESTORE_PRODUCTS);

    	JsonPath js1 =resp.jsonPath();
    	List<Integer> ids =js1.getList("id");
		List<Number> prices =js1.getList("price");
		List<Integer> Count =js1.getList("rating.count");
		List<Number> rate =js1.getList("rating.rate");

		// assert on structure, not on catalogue values that can change at any time
		Assert.assertFalse(ids.isEmpty(), "product list should not be empty");
		Assert.assertEquals(prices.size(), ids.size());
		Assert.assertEquals(Count.size(), ids.size());
		Assert.assertEquals(rate.size(), ids.size());
		for (int i=0; i<ids.size(); i++)
		{
			Assert.assertTrue(prices.get(i).doubleValue() >= 0, "price of product " + ids.get(i));
			Assert.assertTrue(Count.get(i) >= 0, "rating.count of product " + ids.get(i));
			Assert.assertTrue(rate.get(i).doubleValue() >= 0 && rate.get(i).doubleValue() <= 5, "rating.rate of product " + ids.get(i));
		}
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(createdUserId);
		createdUserId = null;
	}
}
