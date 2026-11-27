package getAPITest;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import io.restassured.response.Response;

import io.restassured.specification.RequestSpecification;

public class GetAPIWithoutBDD {

	@Test
	public void withoutBDD() {
		RequestSpecification ReqSpec = given().spec(RestClient.fakeStore());
		Response resp = ReqSpec.get(Routes.CONTACTS);
		int RespCode = resp.statusCode();
		String statusLine = resp.statusLine();
		
		Assert.assertEquals(RespCode, 404);
		//Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");
		
		
	}
	
}
