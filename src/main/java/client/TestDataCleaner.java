package client;

import static io.restassured.RestAssured.given;

import constants.Routes;

/**
 * Deletes data that tests create on shared public APIs, so runs do not pile up records.
 * Call it from @AfterMethod. The response is not asserted on purpose: cleanup must
 * never hide or change the real test result.
 */
public final class TestDataCleaner {

	private TestDataCleaner() {
	}

	public static void deleteGoRestUser(Integer userId) {
		if (userId == null) {
			return;
		}
		given().spec(RestClient.gorest())
		  .pathParam("userId", userId)
		    .when()
		      .delete(Routes.GOREST_USER_BY_ID);
	}

	public static void deleteBooking(Integer bookingId, String token) {
		if (bookingId == null || token == null) {
			return;
		}
		given().spec(RestClient.booking(token))
		  .pathParam("bookingId", bookingId)
		    .when()
		      .delete(Routes.BOOKING_BY_ID);
	}
}
