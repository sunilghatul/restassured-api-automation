package utils;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Turns a JSON array file from src/test/resources into TestNG DataProvider rows.
 *
 *   [ {"case": "invalid email", "email": "abc", ...}, {...} ]
 *
 *   &#64;DataProvider
 *   public Object[][] data() { return JsonDataReader.toDataProvider("testdata/x.json"); }
 *
 *   &#64;Test(dataProvider = "data")
 *   public void test(Map&lt;String, Object&gt; row) { ... }
 */
public final class JsonDataReader {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JsonDataReader() {
	}

	public static List<Map<String, Object>> readList(String resourcePath) {
		try {
			return MAPPER.readValue(FileUtil.readAsString(resourcePath), new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			throw new IllegalStateException("Unable to parse test data file: " + resourcePath, e);
		}
	}

	public static Object[][] toDataProvider(String resourcePath) {
		List<Map<String, Object>> rows = readList(resourcePath);
		Object[][] data = new Object[rows.size()][1];
		for (int i = 0; i < rows.size(); i++) {
			data[i][0] = rows.get(i);
		}
		return data;
	}
}
