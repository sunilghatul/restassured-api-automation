package negativeTests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import pojo.PoJoPojO;
import utils.FileUtil;
import utils.TestDataUtil;

/**
 * Negative cases for Restful-Booker: missing / invalid token, unknown booking, bad credentials.
 */
public class BookingNegativeTest {

	private String validToken;
	private Integer bookingId;

	@BeforeClass(alwaysRun = true)
	public void createBooking()
	{
		validToken = given().spec(RestClient.booking())
		  .body(FileUtil.getFile("json/Auth.json"))
		    .when()
		      .post(Routes.BOOKING_AUTH)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .extract()
		              .path("token");

		bookingId = given().spec(RestClient.booking())
		  .body(TestDataUtil.randomBooking())
		    .when()
		      .post(Routes.BOOKING)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .extract()
		              .path("bookingid");
	}

	// ------------------------------------------------------------------
	// Missing / invalid token - 403
	// ------------------------------------------------------------------

	@Test(groups = "negative")
	public void updateBooking_withoutToken_returns403()
	{
		given().spec(RestClient.booking())
		  .pathParam("bookingId", bookingId)
		  .body(TestDataUtil.randomBooking())
		    .when()
		      .put(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.status(403))
		            .body(equalTo("Forbidden"));
	}

	@Test(groups = "negative")
	public void updateBooking_withInvalidToken_returns403()
	{
		given().spec(RestClient.booking("invalid-token"))
		  .pathParam("bookingId", bookingId)
		  .body(TestDataUtil.randomBooking())
		    .when()
		      .put(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.status(403))
		            .body(equalTo("Forbidden"));
	}

	@Test(groups = "negative")
	public void patchBooking_withoutToken_returns403()
	{
		given().spec(RestClient.booking())
		  .pathParam("bookingId", bookingId)
		  .body(Map.of("firstname", "Hacker"))
		    .when()
		      .patch(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.status(403));
	}

	@Test(groups = "negative")
	public void deleteBooking_withoutToken_returns403()
	{
		given().spec(RestClient.booking())
		  .pathParam("bookingId", bookingId)
		    .when()
		      .delete(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.status(403));

		// ...and the booking must still exist
		given().spec(RestClient.booking())
		  .pathParam("bookingId", bookingId)
		    .when()
		      .get(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.ok());
	}

	// ------------------------------------------------------------------
	// Unknown booking - 404
	// ------------------------------------------------------------------

	@Test(groups = "negative")
	public void getBooking_withUnknownId_returns404()
	{
		given().spec(RestClient.booking())
		  .pathParam("bookingId", 999999999)
		    .when()
		      .get(Routes.BOOKING_BY_ID)
		        .then()
		          .spec(ResponseSpecs.status(404))
		            .body(equalTo("Not Found"));
	}

	// ------------------------------------------------------------------
	// Auth
	// ------------------------------------------------------------------

	@Test(groups = "negative")
	public void createToken_withWrongPassword_returnsBadCredentials()
	{
		// Restful-Booker answers 200 with a "reason" instead of a 401
		given().spec(RestClient.booking())
		  .body(new PoJoPojO("admin", "wrong-password"))
		    .when()
		      .post(Routes.BOOKING_AUTH)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .body("reason", equalTo("Bad credentials"));
	}

	@Test(groups = "negative")
	public void createToken_withEmptyBody_returnsBadCredentials()
	{
		given().spec(RestClient.booking())
		  .body("{}")
		    .when()
		      .post(Routes.BOOKING_AUTH)
		        .then()
		          .spec(ResponseSpecs.ok())
		            .body("reason", equalTo("Bad credentials"));
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp()
	{
		TestDataCleaner.deleteBooking(bookingId, validToken);
	}
}
