package bookingApisFlow;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import client.ResponseSpecs;
import client.RestClient;
import client.TestDataCleaner;
import constants.Routes;
import constants.SchemaPaths;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pojo.Booking;
import utils.FileUtil;
import utils.TestDataUtil;

public class BookingApiFeatureTest {

	private String tokenId;
	private final List<Integer> createdBookings = new ArrayList<>();

	@BeforeMethod
	public void getToken()
	{
		tokenId = given().spec(RestClient.booking())
		    .body(FileUtil.getFile("json/Auth.json"))
		       .when()
		          .post(Routes.BOOKING_AUTH)
		             .then()
		               .spec(ResponseSpecs.ok())
		                 .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTH_TOKEN))
		                    .extract()
		                     .path("token");
	}

	@Test(groups = "smoke")
	public void getBookingIdTest()
	{
		Response resp =given().spec(RestClient.booking())
		 .when()
		  .get(Routes.BOOKING)
		    .then()
		     .spec(ResponseSpecs.ok())
		         .body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING_IDS))
		      .extract()
		         .response();

		JsonPath js = resp.jsonPath();
		List<Integer> bokkingIdList = js.getList("bookingid");
		Assert.assertFalse(bokkingIdList.isEmpty(), "Booking id list should not be empty");
	}

	/** Creates a booking from json/booking.json (firstname Jim, additionalneeds Breakfast). */
	public int createBooking()
	{
		int bookingId =given().spec(RestClient.booking())
	     .body(FileUtil.getFile("json/booking.json"))
		  .when()
		   .post(Routes.BOOKING)
		    .then()
		     .spec(ResponseSpecs.ok())
		         .body(matchesJsonSchemaInClasspath(SchemaPaths.CREATE_BOOKING))
		      .extract()
		         .path("bookingid");

		createdBookings.add(bookingId);
		return bookingId;
	}

	/** Creates a booking from a POJO and returns its id. */
	public int createBooking(Booking booking)
	{
		int bookingId =given().spec(RestClient.booking())
	     .body(booking)
		  .when()
		   .post(Routes.BOOKING)
		    .then()
		     .spec(ResponseSpecs.ok())
		       .body("booking.firstname", equalTo(booking.getFirstname()))
		       .body("booking.totalprice", equalTo(booking.getTotalprice()))
		      .extract()
		         .path("bookingid");

		createdBookings.add(bookingId);
		return bookingId;
	}

		@Test
		public void getBookingTest()
		{
			int BookingId = createBooking();
			 given().spec(RestClient.booking())
			  .pathParam("bookingId",BookingId)
			    .get(Routes.BOOKING_BY_ID)
			       .then()
			         .spec(ResponseSpecs.ok())
			             .and()
			               .body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING))
			               .body("firstname", equalTo("Jim"))
			                 .and()
			                   .body("additionalneeds",  equalTo("Breakfast"));
		}

		@Test(groups = "smoke")
		public void CreateBookingTest()
		{
			Assert.assertTrue(createBooking() > 0, "Booking id should be a positive number");
		}

		@Test
		public void CreateBookingWithPOJOTest()
		{
			Booking booking = TestDataUtil.randomBooking();
			int BookingId = createBooking(booking);

			Booking fromApi = given().spec(RestClient.booking())
			  .pathParam("bookingId", BookingId)
			    .get(Routes.BOOKING_BY_ID)
			      .then()
			        .spec(ResponseSpecs.ok())
			          .extract()
			            .as(Booking.class);

			Assert.assertEquals(fromApi, booking, "booking read back should equal the booking sent");
		}

		@Test
		public void updateBooking()
		{
			int BookingId = createBooking();

			Booking updated = Booking.builder()
					.firstname("Jack")
					.lastname("Shukla")
					.totalprice(111)
					.depositpaid(true)
					.bookingdates(new Booking.BookingDates("2018-01-01", "2019-01-01"))
					.additionalneeds("Lunch")
					.build();

		//Update The Booking (PUT needs the auth token cookie -> RestClient.booking(token))
			given().spec(RestClient.booking(tokenId))
			 .pathParam("bookingId", BookingId)
			  .body(updated)
			   .when()
			     .put(Routes.BOOKING_BY_ID)
			       .then()
			         .spec(ResponseSpecs.ok())
			             .and()
			               .body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING))
			               .body("firstname", equalTo("Jack"))
			                 .and()
			                   .body("lastname", equalTo("Shukla"))
			                   .body("additionalneeds", equalTo("Lunch"));
		}

		@Test
		public void partialUpdateBooking()
		{
			int BookingId = createBooking();

			// PATCH changes only the given fields, the rest of json/booking.json stays as it was
			given().spec(RestClient.booking(tokenId))
			 .pathParam("bookingId", BookingId)
			  .body(Map.of("firstname", "Ravi", "additionalneeds", "Dinner"))
			   .when()
			     .patch(Routes.BOOKING_BY_ID)
			       .then()
			         .spec(ResponseSpecs.ok())
			           .body("firstname", equalTo("Ravi"))
			           .body("additionalneeds", equalTo("Dinner"))
			           .body("lastname", equalTo("Brown"))
			           .body("totalprice", equalTo(111));
		}

		@Test
		public void deleteBooking()
		{
			int BookingId = createBooking();

			given().spec(RestClient.booking(tokenId))
			  .pathParam("bookingId", BookingId)
			   .when()
			     .delete(Routes.BOOKING_BY_ID)
			       .then()
			         .spec(ResponseSpecs.status(201));

			given().spec(RestClient.booking())
			  .pathParam("bookingId", BookingId)
			   .when()
			     .get(Routes.BOOKING_BY_ID)
			       .then()
			         .spec(ResponseSpecs.status(404));
		}

		@AfterMethod(alwaysRun = true)
		public void cleanUp()
		{
			createdBookings.forEach(id -> TestDataCleaner.deleteBooking(id, tokenId));
			createdBookings.clear();
		}
}
