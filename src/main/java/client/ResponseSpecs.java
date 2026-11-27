package client;

import static org.hamcrest.Matchers.lessThan;

import java.util.concurrent.TimeUnit;

import config.ConfigReader;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

/**
 * Reusable response expectations: status code + content type + response-time SLA
 * (max.response.time.ms in config.properties).
 *
 *   .then().spec(ResponseSpecs.ok())
 */
public final class ResponseSpecs {

	private ResponseSpecs() {
	}

	/** 200 + JSON */
	public static ResponseSpecification ok() {
		return json(200);
	}

	/** 201 + JSON */
	public static ResponseSpecification created() {
		return json(201);
	}

	/** 204, no body */
	public static ResponseSpecification noContent() {
		return status(204);
	}

	/** Any status code + JSON body, e.g. 401 / 404 / 422 error responses */
	public static ResponseSpecification json(int statusCode) {
		return base(statusCode).expectContentType(ContentType.JSON).build();
	}

	/** Status code + response time only (plain-text / HTML bodies such as Restful-Booker's "Forbidden") */
	public static ResponseSpecification status(int statusCode) {
		return base(statusCode).build();
	}

	private static ResponseSpecBuilder base(int statusCode) {
		return new ResponseSpecBuilder()
				.expectStatusCode(statusCode)
				.expectResponseTime(lessThan(ConfigReader.getLong("max.response.time.ms", 10000)), TimeUnit.MILLISECONDS);
	}
}
