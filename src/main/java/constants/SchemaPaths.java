package constants;

/**
 * Classpath locations of JSON schemas under src/test/resources/schemas.
 * Use with: body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKING))
 */
public final class SchemaPaths {

	private SchemaPaths() {
	}

	public static final String AUTH_TOKEN = "schemas/auth-token-schema.json";
	public static final String BOOKING = "schemas/booking-schema.json";
	public static final String CREATE_BOOKING = "schemas/create-booking-schema.json";
	public static final String BOOKING_IDS = "schemas/booking-ids-schema.json";
	public static final String GOREST_USER = "schemas/gorest-user-schema.json";
	public static final String GOREST_USER_LIST = "schemas/gorest-user-list-schema.json";
	public static final String FAKESTORE_PRODUCT = "schemas/fakestore-product-schema.json";
	public static final String FAKESTORE_PRODUCT_LIST = "schemas/fakestore-product-list-schema.json";
	public static final String REQRES_USER_LIST = "schemas/reqres-user-list-schema.json";
}
