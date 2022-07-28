package services;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class TestBase {

    public static RequestSpecification SPEC = new RequestSpecBuilder()
            .addHeader("accept", "application/json")
            .setBaseUri("https://bookstore.toolsqa.com").build();

}
