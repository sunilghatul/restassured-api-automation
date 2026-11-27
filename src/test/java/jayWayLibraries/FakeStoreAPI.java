package jayWayLibraries;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import client.RestClient;
import constants.Routes;
import io.restassured.response.Response;


public class FakeStoreAPI {


	@Test
	public void JsonPathtest()
	{
		Response response =given().spec(RestClient.fakeStore())
		  .when()
		    .get(Routes.FAKESTORE_PRODUCTS);

		Assert.assertEquals(response.statusCode(), 200);
		DocumentContext	ctx =JsonPath.parse(response.asString());

	// filter: price > 50
	List<Number> PriceList	=ctx.read("$[?(@.price>50)].price");
	List<Integer> Idlist	=ctx.read("$[?(@.price>50)].id");
	List<String> titleList	=ctx.read("$[?(@.price>50)].title");
	PriceList.forEach(p -> Assert.assertTrue(p.doubleValue() > 50, "filtered price should be > 50 but was " + p));
	Assert.assertEquals(Idlist.size(), PriceList.size());
	Assert.assertEquals(titleList.size(), PriceList.size());

	// wildcard: all titles / prices
	List<String> titleList1	=ctx.read("$.[*].title");
	List<Number> Pricelist1	=ctx.read("$.[*].price");
	Assert.assertFalse(titleList1.isEmpty());
	Assert.assertEquals(Pricelist1.size(), titleList1.size());
	Assert.assertTrue(Pricelist1.size() >= PriceList.size(), "filtered list cannot be bigger than the full list");

	// nested filter: rating.rate < 4.0
	List <Number> RateList = ctx.read("$.[?(@.rating.rate<4.0)].rating.rate");
	RateList.forEach(r -> Assert.assertTrue(r.doubleValue() < 4.0, "filtered rate should be < 4 but was " + r));

	// projection: two attributes per match
	List<Map<String, Object>> twoAttributes = ctx.read("$.[?(@.rating.rate<4.0)].['title' , 'price']");
	Assert.assertEquals(twoAttributes.size(), RateList.size());
	for(Map<String, Object> attriList : twoAttributes)
	{
		Assert.assertTrue(attriList.get("title") instanceof String, "title");
		Assert.assertTrue(attriList.get("price") instanceof Number, "price");
	}

	// AND operator
	List <Map<String, Object>> AndOperators = ctx.read("$[?((@.category == 'jewelery') && (@.price>50))].['id', 'price', 'title', 'category']");
	for (Map<String, Object> product : AndOperators)
	{
		Assert.assertEquals(product.get("category"), "jewelery");
		Assert.assertTrue(((Number) product.get("price")).doubleValue() > 50);
	}
	}
}
