package schemaValidation;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import io.restassured.http.ContentType;
import utils.FileUtil;
import constants.SchemaPaths;

/**
 * JSON Schema validation cases.
 * Schemas live in src/test/resources/schemas (draft-04, supported by rest-assured json-schema-validator).
 */
public class SchemaValidationTest {

	// ------------------------------------------------------------------
	// Restful-Booker
	// ------------------------------------------------------------------

	@Test(groups = "schema")
	public void authTokenSchemaTest()
	{
		given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		  .body(FileUtil.getFile("json/Auth.json"))
		    .when()
		      .post(Routes.BOOKING_AUTH)
		        .then()
		          .assertThat()
		            .statusCode(200)
		              .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTH_TOKEN));
	}

	@Test(groups = "schema")
	public void getBookingIdsSchemaTest()
	{
		given().spec(RestClient.booking())
		  .when()
		    .get(Routes.BOOKING)
		      .then()
		        .assertThat()
		          .statusCode(200)
		            .body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING_IDS));
	}

	@Test(groups = "schema")
	public void createAndGetBookingSchemaTest()
	{
		int bookingId = given().spec(RestClient.booking())
		 .contentType(ContentType.JSON)
		  .body(FileUtil.getFile("json/booking.json"))
		    .when()
		      .post(Routes.BOOKING)
		        .then()
		          .assertThat()
		            .statusCode(200)
		              .body(matchesJsonSchemaInClasspath(SchemaPaths.CREATE_BOOKING))
		                .extract()
		                  .path("bookingid");

		given().spec(RestClient.booking())
		 .pathParam("bookingId", bookingId)
		   .when()
		     .get(Routes.BOOKING_BY_ID)
		       .then()
		         .assertThat()
		           .statusCode(200)
		             .body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING));
	}

	// ------------------------------------------------------------------
	// GoRest
	// ------------------------------------------------------------------

	@Test(groups = "schema")
	public void gorestUserListSchemaTest()
	{
		given().spec(RestClient.gorest())
		   .when()
		     .get(Routes.GOREST_USERS)
		       .then()
		         .assertThat()
		           .statusCode(200)
		             .body(matchesJsonSchemaInClasspath(SchemaPaths.GOREST_USER_LIST));
	}

	// ------------------------------------------------------------------
	// FakeStore
	// ------------------------------------------------------------------

	@Test(groups = "schema")
	public void fakeStoreProductListSchemaTest()
	{
		given().spec(RestClient.fakeStore())
		  .when()
		    .get(Routes.FAKESTORE_PRODUCTS)
		      .then()
		        .assertThat()
		          .statusCode(200)
		            .body(matchesJsonSchemaInClasspath(SchemaPaths.FAKESTORE_PRODUCT_LIST));
	}

	@Test(groups = "schema")
	public void fakeStoreSingleProductSchemaTest()
	{
		given().spec(RestClient.fakeStore())
		 .pathParam("productId", 1)
		  .when()
		    .get(Routes.FAKESTORE_PRODUCT_BY_ID)
		      .then()
		        .assertThat()
		          .statusCode(200)
		            .body(matchesJsonSchemaInClasspath(SchemaPaths.FAKESTORE_PRODUCT));
	}

	// ------------------------------------------------------------------
	// ReqRes
	// ------------------------------------------------------------------

	@Test(groups = "schema")
	public void reqresUserListSchemaTest()
	{
		given().spec(RestClient.reqres())
		  .queryParam("page", 1)
		    .when()
		      .get(Routes.REQRES_USERS)
		        .then()
		          .assertThat()
		            .statusCode(200)
		              .body(matchesJsonSchemaInClasspath(SchemaPaths.REQRES_USER_LIST));
	}

	// ------------------------------------------------------------------
	// Negative cases (offline) - prove the schemas actually catch bad payloads
	// ------------------------------------------------------------------

	@Test(groups = "schema")
	public void bookingSchema_shouldRejectWrongDataTypes()
	{
		String invalidBooking = "{"
				+ "\"firstname\":\"Jim\","
				+ "\"lastname\":\"Brown\","
				+ "\"totalprice\":\"one hundred\","          // should be number
				+ "\"depositpaid\":\"yes\","                 // should be boolean
				+ "\"bookingdates\":{\"checkin\":\"2018-01-01\",\"checkout\":\"2019-01-01\"}"
				+ "}";

		assertThat(invalidBooking, not(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING)));
	}

	@Test(groups = "schema")
	public void bookingSchema_shouldRejectMissingRequiredField()
	{
		String missingDates = "{\"firstname\":\"Jim\",\"lastname\":\"Brown\",\"totalprice\":111,\"depositpaid\":true}";

		assertThat(missingDates, not(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING)));
	}

	@Test(groups = "schema")
	public void gorestUserSchema_shouldRejectInvalidEnumValue()
	{
		String invalidUser = "{\"id\":1,\"name\":\"Sunil\",\"email\":\"a@b.com\",\"gender\":\"unknown\",\"status\":\"active\"}";

		assertThat(invalidUser, not(matchesJsonSchemaInClasspath(SchemaPaths.GOREST_USER)));
	}

	@Test(groups = "schema")
	public void bookingSchema_shouldAcceptValidPayloadFile()
	{
		assertThat(FileUtil.readAsString("json/booking.json"), matchesJsonSchemaInClasspath(SchemaPaths.BOOKING));
	}
}
