package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Loads files from src/test/resources via the classpath, so paths work on
 * any OS / machine (no more ".\\src\\test\\resource\\..." paths).
 */
public final class FileUtil {

	private FileUtil() {
	}

	/** e.g. FileUtil.getFile("json/booking.json") */
	public static File getFile(String resourcePath) {
		URL url = FileUtil.class.getClassLoader().getResource(resourcePath);
		if (url == null) {
			throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
		}
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Invalid resource path: " + resourcePath, e);
		}
	}

	/** Returns the resource content as a String, e.g. for editing a JSON payload before sending. */
	public static String readAsString(String resourcePath) {
		try (InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
			}
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read resource: " + resourcePath, e);
		}
	}
}
