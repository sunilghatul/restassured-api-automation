package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import config.ConfigReader;

/**
 * Re-runs a failed test up to retry.count times (config.properties).
 * The practice APIs are public and sometimes answer 5xx / time out, which is not a test failure.
 * A retried attempt shows up as "skipped" in the report; only the last attempt decides pass/fail.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

	private static final int MAX_RETRIES = ConfigReader.getInt("retry.count", 1);
	private int attempt = 0;

	@Override
	public boolean retry(ITestResult result) {
		if (attempt < MAX_RETRIES) {
			attempt++;
			System.out.println("Retrying " + result.getMethod().getQualifiedName()
					+ " (attempt " + (attempt + 1) + " of " + (MAX_RETRIES + 1) + ")");
			return true;
		}
		return false;
	}
}
