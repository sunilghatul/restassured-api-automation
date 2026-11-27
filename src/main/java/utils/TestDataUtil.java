package utils;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import config.ConfigReader;
import net.datafaker.Faker;
import pojo.Booking;
import pojo.GoRestUser;

/**
 * Random but realistic test data (Datafaker), so tests never collide on unique fields.
 */
public final class TestDataUtil {

	private static final Faker FAKER = new Faker();
	private static final AtomicInteger COUNTER = new AtomicInteger();

	private TestDataUtil() {
	}

	/** Unique e-mail so create-user calls never fail with "has already been taken". */
	public static String randomEmail(String prefix) {
		return prefix + System.currentTimeMillis() + COUNTER.incrementAndGet() + ConfigReader.get("email.domain");
	}

	public static String randomEmail() {
		return randomEmail(FAKER.name().firstName().toLowerCase().replaceAll("[^a-z]", ""));
	}

	public static String randomFirstName() {
		return FAKER.name().firstName();
	}

	public static String randomLastName() {
		return FAKER.name().lastName();
	}

	public static String randomGender() {
		return FAKER.bool().bool() ? "male" : "female";
	}

	/** A valid, unique GoRest user. */
	public static GoRestUser randomGoRestUser() {
		return GoRestUser.builder()
				.name(randomFirstName() + " " + randomLastName())
				.email(randomEmail())
				.gender(randomGender())
				.status("active")
				.build();
	}

	/** A valid Restful-Booker booking with future dates. */
	public static Booking randomBooking() {
		LocalDate checkin = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 30));
		return Booking.builder()
				.firstname(randomFirstName())
				.lastname(randomLastName())
				.totalprice(FAKER.number().numberBetween(50, 1000))
				.depositpaid(FAKER.bool().bool())
				.bookingdates(Booking.BookingDates.builder()
						.checkin(checkin.toString())
						.checkout(checkin.plusDays(FAKER.number().numberBetween(1, 10)).toString())
						.build())
				.additionalneeds(FAKER.options().option("Breakfast", "Lunch", "Dinner", "Parking"))
				.build();
	}
}
