package deserilization;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.ResponseSpecs;
import client.RestClient;
import constants.Routes;
import io.restassured.response.Response;
import pojo.GoRestUser;

public class getDatainArray {


	@Test
	public void getDataFromArray() throws JsonProcessingException
	{
	Response res1 =given().spec(RestClient.gorest())
			      .when()
	                .get(Routes.GOREST_USERS)
	                  .then()
	                    .spec(ResponseSpecs.ok())
	                      .extract()
	                        .response();

	// de-serialization of a JSON array into POJO[]
	ObjectMapper mapper  = new ObjectMapper();
	GoRestUser [] users =mapper.readValue(res1.getBody().asString(), GoRestUser[].class);

	Assert.assertTrue(users.length > 0, "User list should not be empty");
	for (GoRestUser e : users)
	{
		Assert.assertNotNull(e.getId(), "id");
		Assert.assertNotNull(e.getName(), "name");
		Assert.assertTrue(e.getEmail().contains("@"), "email should be valid: " + e.getEmail());
		Assert.assertTrue(e.getGender().matches("male|female"), "gender: " + e.getGender());
		Assert.assertTrue(e.getStatus().matches("active|inactive"), "status: " + e.getStatus());
	}
}
}
