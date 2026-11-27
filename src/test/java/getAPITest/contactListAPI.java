package getAPITest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;

public class contactListAPI {
	
	
	@Test
	public void contactListTest_withoutToken()
	{
		//URL = "https://thinking-tester-contact-list.herokuapp.com/contacts"
		given().spec(RestClient.contactList())
		  .when()
		    .get(Routes.CONTACTS)
		      .then()
		        .assertThat()
		          .statusCode(401)
		            .and()
		              .body("error", equalTo("Please authenticate."));
	}

}
