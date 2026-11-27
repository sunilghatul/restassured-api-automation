package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Layered configuration. For a key such as "gorest.token" the first non-blank value wins:
 *
 *   1. JVM system property        -Dgorest.token=...
 *   2. Environment variable       GOREST_TOKEN=...        (dots -> underscores, upper-case)
 *   3. config.local.properties    git-ignored, for secrets on your own machine
 *   4. config-{env}.properties    only when -Denv=qa / ENV=qa is given
 *   5. config.properties          committed defaults
 */
public final class ConfigReader {

	private static final Properties PROPS = new Properties();

	static {
		load("config.properties", true);

		String env = firstNonBlank(System.getProperty("env"), System.getenv("ENV"));
		if (env != null) {
			load("config-" + env.trim().toLowerCase() + ".properties", true);
		}

		load("config.local.properties", false);
	}

	private ConfigReader() {
	}

	public static String get(String key) {
		String value = getOrNull(key);
		if (value == null) {
			throw new IllegalArgumentException("Missing config value for key '" + key + "'. Set it in config.properties, "
					+ "config.local.properties, -D" + key + "=... or env var " + toEnvName(key));
		}
		return value;
	}

	/** Returns the value, or {@code null} when it is not set anywhere. */
	public static String getOrNull(String key) {
		String value = firstNonBlank(System.getProperty(key), System.getenv(toEnvName(key)), PROPS.getProperty(key));
		return value == null ? null : value.trim();
	}

	public static String get(String key, String defaultValue) {
		String value = getOrNull(key);
		return value == null ? defaultValue : value;
	}

	public static int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public static int getInt(String key, int defaultValue) {
		String value = getOrNull(key);
		return value == null ? defaultValue : Integer.parseInt(value);
	}

	public static long getLong(String key, long defaultValue) {
		String value = getOrNull(key);
		return value == null ? defaultValue : Long.parseLong(value);
	}

	private static void load(String file, boolean required) {
		try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(file)) {
			if (in == null) {
				if (required) {
					throw new IllegalStateException(file + " not found on the classpath");
				}
				return;
			}
			Properties layer = new Properties();
			layer.load(in);
			// later layers override earlier ones, but blank values never override
			layer.forEach((k, v) -> {
				if (v != null && !v.toString().trim().isEmpty()) {
					PROPS.put(k, v);
				} else {
					PROPS.putIfAbsent(k, v);
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load " + file, e);
		}
	}

	private static String toEnvName(String key) {
		return key.toUpperCase().replace('.', '_');
	}

	private static String firstNonBlank(String... values) {
		for (String v : values) {
			if (v != null && !v.trim().isEmpty()) {
				return v;
			}
		}
		return null;
	}
}
