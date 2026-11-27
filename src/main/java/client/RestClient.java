package client;

import config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Central place that builds a ready-to-use RequestSpecification for every API under test
 * (base URI, content type and auth), so tests only describe the request itself:
 *
 *   given().spec(RestClient.gorest())
 *     .body(user)
 *   .when()
 *     .post("/public/v2/users")
 *   .then()
 *     .statusCode(201);
 *
 * Each call returns a NEW spec, so tests never share state and no test depends on the
 * static RestAssured.baseURI set by another test.
 */
public final class RestClient {

	private static final boolean LOG_ALL = "all".equalsIgnoreCase(ConfigReader.get("log.level", "failures"));

	static {
		if (!LOG_ALL) {
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		}
	}

	private RestClient() {
	}

	// ------------------------------------------------------------------
	// Restful-Booker
	// ------------------------------------------------------------------

	/** Restful-Booker without auth (GET / POST / auth calls). */
	public static RequestSpecification booking() {
		return jsonSpec("booking.base.url").build();
	}

	/** Restful-Booker with the token cookie required by PUT / PATCH / DELETE. */
	public static RequestSpecification booking(String token) {
		return jsonSpec("booking.base.url")
				.addCookie("token", token)
				.build();
	}

	// ------------------------------------------------------------------
	// GoRest (Bearer token)
	// ------------------------------------------------------------------

	public static RequestSpecification gorest() {
		return jsonSpec("gorest.base.url")
				.addHeader("Authorization", "Bearer " + ConfigReader.get("gorest.token"))
				.build();
	}

	/** GoRest without the Authorization header - for 401 negative tests. */
	public static RequestSpecification gorestWithoutAuth() {
		return jsonSpec("gorest.base.url").build();
	}

	// ------------------------------------------------------------------
	// ReqRes (x-api-key header)
	// ------------------------------------------------------------------

	public static RequestSpecification reqres() {
		return jsonSpec("reqres.base.url")
				.addHeader("x-api-key", ConfigReader.get("reqres.api.key"))
				.build();
	}

	// ------------------------------------------------------------------
	// Public APIs without auth
	// ------------------------------------------------------------------

	public static RequestSpecification fakeStore() {
		return jsonSpec("fakestore.base.url").build();
	}

	public static RequestSpecification contactList() {
		return jsonSpec("contactlist.base.url").build();
	}

	/** httpbin: no default content type, each payload test sets its own (multipart / xml / text / html). */
	public static RequestSpecification httpbin() {
		return baseSpec("httpbin.base.url").build();
	}

	/** the-internet.herokuapp.com: returns HTML, so no JSON content type. */
	public static RequestSpecification herokuApp() {
		return baseSpec("herokuapp.base.url").build();
	}

	// ------------------------------------------------------------------
	// Builders
	// ------------------------------------------------------------------

	private static RequestSpecBuilder baseSpec(String baseUrlKey) {
		RequestSpecBuilder builder = new RequestSpecBuilder()
				.setBaseUri(ConfigReader.get(baseUrlKey))
				// attaches every request/response to the Allure report
				.addFilter(new AllureRestAssured());

		// log.level=all -> log every call; anything else -> log only when a validation fails
		if (LOG_ALL) {
			builder.addFilter(new RequestLoggingFilter()).addFilter(new ResponseLoggingFilter());
		}
		return builder;
	}

	private static RequestSpecBuilder jsonSpec(String baseUrlKey) {
		return baseSpec(baseUrlKey)
				.setContentType(ContentType.JSON)
				// plain "application/json": ContentType.JSON expands to 4 mime types and
				// Restful-Booker answers that with 418 I'm a teapot
				.setAccept("application/json");
	}
}
