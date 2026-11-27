# REST Assured API Automation

API test automation with **REST Assured 5 + TestNG + Jackson/Lombok + Allure**, covering Restful-Booker, GoRest, FakeStore, ReqRes, httpbin and the Contact List API — positive, negative and JSON-schema cases, run on GitHub Actions.

## Project structure

```
src/main/java                    Framework layer (reusable, no tests)
├── client/
│   ├── RestClient               Request specs per API: base URI, content type, auth, Allure filter, logging
│   ├── ResponseSpecs            ok() / created() / noContent() / json(code) / status(code) + response-time SLA
│   └── TestDataCleaner          Deletes users / bookings created by tests
├── config/ConfigReader          Layered config (-D > env var > local file > env profile > defaults)
├── constants/
│   ├── Routes                   Endpoint paths
│   └── SchemaPaths              JSON schema locations
├── pojo/                        GoRestUser, Booking, PoJoPojO (auth request)
└── utils/
    ├── FileUtil                 Classpath file loading
    ├── JsonDataReader           JSON file -> TestNG DataProvider
    └── TestDataUtil             Random data with Datafaker

src/main/resources
├── config.properties            Committed defaults (no secrets)
├── config-qa.properties         Example environment profile (-Denv=qa)
└── config.local.properties      Your secrets - git-ignored, create it yourself

src/test/java                    Tests
├── listeners/                   RetryAnalyzer + RetryListener (re-run flaky failures)
├── negativeTests/               401 / 403 / 404 / 422 / 400 cases (GoRest, Booking, ReqRes)
├── schemaValidation/            JSON schema validation (positive + negative)
├── bookingApisFlow/             Restful-Booker CRUD: create, get, PUT, PATCH, delete
├── authAPI/                     Basic auth
├── createUserPOJO/              Auth token (raw JSON / file / POJO), create user
├── updateUserPOJO/              GoRest PUT / PATCH / DELETE
├── mylombok/                    Lombok builder + setters
├── fakeStoreAPIWitLomBok/       Nested Lombok POJOs
├── deserilization/              Jackson ObjectMapper deserialization
├── jsonArrayAPI/                JSON array payloads
├── jayWayLibraries/             Jayway JsonPath filter expressions
├── getAPITest/                  GET, query/path params, non-BDD style
└── passDiffdataAsPayload        Multipart, HTML, XML, text payloads

src/test/resources
├── json/                        Request payloads
├── schemas/                     JSON schemas (draft-04)
├── testdata/                    Data-driven test data
└── files/                       Upload fixtures
```

## Setup

GoRest needs a personal token (free at https://gorest.co.in/consumer/login). Provide it in **one** of these ways:

```bash
# 1. local file (git-ignored)
echo "gorest.token=<your-token>" > src/main/resources/config.local.properties

# 2. environment variable
export GOREST_TOKEN=<your-token>

# 3. JVM property
mvn test -Dgorest.token=<your-token>
```

## Running

| Command | What runs |
|---|---|
| `mvn test` | Full regression (`testng.xml`) |
| `mvn test -DsuiteXmlFile=testng-smoke.xml` | Smoke: one health check per API (`groups = "smoke"`) |
| `mvn test -DsuiteXmlFile=testng-negative.xml` | Negative cases only |
| `mvn test -DsuiteXmlFile=testng-schema.xml` | Schema validation only |
| `mvn test -Dtest=BookingApiFeatureTest` | A single class |
| `mvn test -Denv=qa` | Use `config-qa.properties` on top of the defaults |
| `mvn test -Dlog.level=all` | Log every request/response (default: only failed validations) |

Classes run in parallel (`parallel="classes"`, 4 threads). Failed tests are retried `retry.count` times (default 1).

## Reports

```bash
mvn allure:serve     # build + open the Allure report in a browser
mvn allure:report    # static report in target/site/allure-maven-plugin
```

Every request and response is attached to the Allure report automatically by the `AllureRestAssured` filter in `RestClient`.

## Writing a test

```java
given().spec(RestClient.gorest())                // base URI + JSON + Bearer token
  .body(TestDataUtil.randomGoRestUser())
.when()
  .post(Routes.GOREST_USERS)
.then()
  .spec(ResponseSpecs.created())                 // 201 + JSON + response time
  .body(matchesJsonSchemaInClasspath(SchemaPaths.GOREST_USER));
```

- Request specs: `booking()`, `booking(token)`, `gorest()`, `gorestWithoutAuth()`, `reqres()`, `fakeStore()`, `contactList()`, `httpbin()`, `herokuApp()`
- Anything a test creates on a shared API should be removed in `@AfterMethod(alwaysRun = true)` via `TestDataCleaner`.
- New schema: add it to `src/test/resources/schemas/` and register the path in `constants.SchemaPaths`.
- Data-driven cases: add a JSON array to `src/test/resources/testdata/` and load it with `JsonDataReader.toDataProvider(...)`.

## CI pipeline (not active)

A ready-made GitHub Actions workflow is kept as a **commented-out template** in `ci/api-tests.yml`.
It is deliberately not enabled, because the tests depend on public practice APIs whose availability is not guaranteed.

When you want to switch it on:

1. Copy `ci/api-tests.yml` to `.github/workflows/api-tests.yml`
2. Uncomment everything below the dashed separator line
3. Add the repository secret **`GOREST_TOKEN`** (Settings → Secrets and variables → Actions)

Once enabled it runs on push / pull request to `main`, daily at 09:00 IST and on demand (pick a suite and environment), publishes a test-results check and uploads the Allure report as an artifact.
