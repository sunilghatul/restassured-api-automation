package deserilization;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.ResponseSpecs;
import client.RestClient;
import constants.Routes;
import deserilization.Product.UserData;
import io.restassured.response.Response;

public class ProductAPIdeserilization {

	@Test
	 public void getMapperforProductAPI() throws JsonProcessingException {
        // Send GET request
        Response res = given().spec(RestClient.reqres())
            .queryParam("page", 1)
              .when()
                .get(Routes.REQRES_USERS)
                  .then()
                    .spec(ResponseSpecs.ok())
                      .extract()
                        .response();

        // Deserialize response body to Product object
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(res.getBody().asString(), Product.class);

        Assert.assertEquals(product.getPage(), 1);
        Assert.assertTrue(product.getPer_page() > 0, "per_page");
        Assert.assertTrue(product.getTotal() >= product.getData().size(), "total >= items on the page");
        Assert.assertTrue(product.getTotal_pages() >= 1, "total_pages");
        Assert.assertEquals(product.getData().size(), product.getPer_page(), "page 1 should be full");
        Assert.assertNotNull(product.getSupport().getUrl(), "support.url");
        Assert.assertNotNull(product.getSupport().getText(), "support.text");

        for (UserData userData : product.getData()) {
            Assert.assertTrue(userData.getId() > 0, "id");
            Assert.assertTrue(userData.getEmail().endsWith("@reqres.in"), "email: " + userData.getEmail());
            Assert.assertFalse(userData.getFirst_name().isEmpty(), "first_name");
            Assert.assertFalse(userData.getLast_name().isEmpty(), "last_name");
            Assert.assertTrue(userData.getAvatar().startsWith("https://"), "avatar: " + userData.getAvatar());
        }
	}

}
