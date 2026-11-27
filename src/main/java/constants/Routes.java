package constants;

/**
 * Endpoint paths of every API under test. Base URLs live in config.properties,
 * the path part lives here so a route change is a one-line fix.
 */
public final class Routes {

	private Routes() {
	}

	// ---------- Restful-Booker ----------
	public static final String BOOKING_AUTH = "/auth";
	public static final String BOOKING = "/booking";
	public static final String BOOKING_BY_ID = "/booking/{bookingId}";

	// ---------- GoRest ----------
	public static final String GOREST_USERS = "/public/v2/users";
	public static final String GOREST_USER_BY_ID = "/public/v2/users/{userId}";

	// ---------- FakeStore ----------
	public static final String FAKESTORE_PRODUCTS = "/products";
	public static final String FAKESTORE_PRODUCT_BY_ID = "/products/{productId}";
	public static final String FAKESTORE_USERS = "/users";
	public static final String FAKESTORE_USER_BY_ID = "/users/{userId}";

	// ---------- ReqRes ----------
	public static final String REQRES_USERS = "/api/users";
	public static final String REQRES_USER_BY_ID = "/api/users/{userId}";
	public static final String REQRES_LOGIN = "/api/login";
	public static final String REQRES_REGISTER = "/api/register";

	// ---------- Others ----------
	public static final String HTTPBIN_POST = "/post";
	public static final String HEROKU_BASIC_AUTH = "/basic_auth";
	public static final String CONTACTS = "/contacts";
}
