package createUserPOJO;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.Assert;
import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import io.restassured.http.ContentType;
import pojo.PoJoPojO;
import config.ConfigReader;
import utils.FileUtil;
import constants.SchemaPaths;

public class GetAuthTokenAPI {

	@Test()
	public void withHardcodedata() {

		String tokenId = given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		    .body("{\n"
		    		+ "    \"username\" : \"" + ConfigReader.get("booking.username") + "\",\n"
		    		+ "    \"password\" : \"" + ConfigReader.get("booking.password") + "\"\n"
		    		+ "}")
		       .when()
		          .post(Routes.BOOKING_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(200)
		                   .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTH_TOKEN))
		                    .extract()
		                     .path("token");

		Assert.assertNotNull(tokenId);
	}

@Test
public void getAuthToken_withJsonFile() {

		String tokenId = given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		    .body(FileUtil.getFile("json/Auth.json"))
		       .when()
		          .post(Routes.BOOKING_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(200)
		                   .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTH_TOKEN))
		                    .extract()
		                     .path("token");

		Assert.assertNotNull(tokenId);

}

@Test(groups = "smoke")
public void getAuthToken_withPOJO() {

	PoJoPojO credentials = new PoJoPojO(ConfigReader.get("booking.username"), ConfigReader.get("booking.password"));

		String tokenId = given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		    .body(credentials)  //here need to do serialization pojo to json ...ObjectMapper
		       .when()
		          .post(Routes.BOOKING_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(200)
		                   .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTH_TOKEN))
		                    .extract()
		                     .path("token");

		Assert.assertNotNull(tokenId);

}

@Test
public void getAuthToken_withInvalidCredentials() {

		// restful-booker returns 200 with {"reason":"Bad credentials"} instead of a 4xx
		String reason = given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		    .body(new PoJoPojO("invalidUser", "invalidPassword"))
		       .when()
		          .post(Routes.BOOKING_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(200)
		                    .extract()
		                     .path("reason");

		Assert.assertEquals(reason, "Bad credentials");
}
}
