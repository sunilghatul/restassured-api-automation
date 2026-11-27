package authAPI;

import static org.hamcrest.Matchers.containsString;

import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import config.ConfigReader;

public class BasicAuth {


	@Test(groups = "smoke")
	public void AuthBasic()
	{
	given().spec(RestClient.herokuApp())
		   .auth()
		     .basic(ConfigReader.get("basic.auth.username"), ConfigReader.get("basic.auth.password"))
		       .when()
		          .get(Routes.HEROKU_BASIC_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(200)
		                   .and()
		                     .body(containsString("Congratulations"));
	}

	@Test
	public void AuthBasic_withInvalidCredentials()
	{
	given().spec(RestClient.herokuApp())
		   .auth()
		     .basic("wrongUser", "wrongPassword")
		       .when()
		          .get(Routes.HEROKU_BASIC_AUTH)
		             .then()
		               .assertThat()
		                 .statusCode(401);
	}
}
