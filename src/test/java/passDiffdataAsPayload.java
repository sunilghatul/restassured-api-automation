import org.testng.annotations.Test;

import client.RestClient;
import constants.Routes;
import config.ConfigReader;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

import utils.FileUtil;

public class passDiffdataAsPayload {
	
	
	@Test
	public void fileUpload()
	{
		given().spec(RestClient.httpbin())
		  .contentType(ContentType.MULTIPART)		    
		      .multiPart("Sunil's File" , FileUtil.getFile(ConfigReader.get("upload.file.path")))
		      .multiPart("Sunil", "API")
		       .when()
		        .post(Routes.HTTPBIN_POST)
		          .then()
		            .statusCode(200);
		       		         
		         
	}
	
	@Test
	public void htmlData ()
	{
		
		String htmlpayload = "My API's can automate";
		given().spec(RestClient.httpbin())
		 .contentType(ContentType.HTML)
		   .body(htmlpayload)
		     .when()
		       .post(Routes.HTTPBIN_POST)
		         .then()
		           .statusCode(200);
		   
	}
	
	@Test
	public void xmlData ()
	{
		
		String Xmlpayload = "<paymentRequest>\n"
				+ "    <PAY_ID>12345</PAY_ID>\n"
				+ "    <CUST_NAME>John Doe</CUST_NAME>\n"
				+ "    <ORDER_ID>ORD98765</ORDER_ID>\n"
				+ "    <AMOUNT>1500</AMOUNT>\n"
				+ "    <CURRENCY>INR</CURRENCY>\n"
				+ "    <EMAIL>johndoe@example.com</EMAIL>\n"
				+ "    <MOBILE_NO>9876543210</MOBILE_NO>\n"
				+ "    <RETURN_URL>https://example.com/callback</RETURN_URL>\n"
				+ "    <PRODUCT_DESC>Electronics Purchase</PRODUCT_DESC>\n"
				+ "</paymentRequest>";
		given().spec(RestClient.httpbin())
		 .contentType("application/xml;charset=utf-8")
		   .body(Xmlpayload)
		     .when()
		       .post(Routes.HTTPBIN_POST)
		         .then()
		           .statusCode(200);
	
	}
	
	@Test
	public void textData ()
	{
		String textPayload = "My name is My name";
		given().spec(RestClient.httpbin())
		 .contentType(ContentType.TEXT)
		   .body(textPayload)
		     .when()
		       .post(Routes.HTTPBIN_POST)
		         .then()
		           .statusCode(200);
		
	}

}
