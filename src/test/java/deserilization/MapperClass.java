package deserilization;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import io.restassured.response.Response;
import pojo.GoRestUser;
import utils.TestDataUtil;

public class MapperClass {

	private Integer userId;

	GoRestUser user = GoRestUser.builder()
			    .name("SunilG")
			    .gender("male")
			    .status("active")
			    .email(TestDataUtil.randomEmail("sg"))
			    .build();


	@Test
	public void getResponse() throws JsonProcessingException
	{
		userId = given().spec(RestClient.gorest())
		      .body(user)
		        .when()
		          .post(Routes.GOREST_USERS)
		            .then()
		              .spec(ResponseSpecs.created())
		                .extract()
		                  .path("id");

		Response res1 =given().spec(RestClient.gorest())
		  .pathParam("userId", userId)
		      .when()
                .get(Routes.GOREST_USER_BY_ID)
                  .then()
                    .spec(ResponseSpecs.ok())
                      .extract()
                        .response();

		ObjectMapper mapper = new ObjectMapper();    //de serialization
		GoRestUser userMapper =	mapper.readValue(res1.getBody().asString(), GoRestUser.class);

		Assert.assertEquals(userMapper.getId(), userId);
		Assert.assertEquals(userMapper.getName(), user.getName());
		Assert.assertEquals(userMapper.getEmail(), user.getEmail());
		Assert.assertEquals(userMapper.getGender(), user.getGender());
		Assert.assertEquals(userMapper.getStatus(), user.getStatus());
	}

	@AfterMethod(alwaysRun = true)
	public void cleanUp() {
		TestDataCleaner.deleteGoRestUser(userId);
	}

}
