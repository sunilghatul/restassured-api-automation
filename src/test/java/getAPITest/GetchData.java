package getAPITest;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class GetchData {

	@Test
	public void fetchdata()
	{
		Response resp = given().spec(RestClient.fakeStore())
		  .get(Routes.FAKESTORE_PRODUCTS);

		JsonPath jsonpath = resp.jsonPath();
		List<Integer> ids = jsonpath.getList("id");
		List<String> titles = jsonpath.getList("title");

		Assert.assertEquals(resp.statusCode(), 200);
		Assert.assertFalse(ids.isEmpty(), "product list should not be empty");
		Assert.assertEquals(ids.stream().distinct().count(), ids.size(), "product ids should be unique");
		titles.forEach(t -> Assert.assertFalse(t.trim().isEmpty(), "title should not be blank"));
	}

}
