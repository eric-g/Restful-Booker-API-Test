package services;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import filters.LoggingFilter;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseService {

    protected String baseUrl = "https://restful-booker.herokuapp.com";
    private final RequestSpecification requestSpecification;

    static {
        Dotenv.configure()
            .ignoreIfMissing() // Ignore if .env file is not found
            .systemProperties() // Load variables to System properties
            .load();
        RestAssured.filters(new LoggingFilter());
    }

    public BaseService() {
        requestSpecification = given().baseUri(baseUrl);
        requestSpecification.contentType(ContentType.JSON);
        
    }

    protected <T> void addQueryParameter(HashMap<String, T> queryParams){
        if(queryParams!=null){
            requestSpecification.queryParams(queryParams);
        }

    }

    protected <T> Response post(T payload, String endpoint) {
        return requestSpecification
                .body(payload)
                .post(endpoint);
    }
    protected Response get(String endpoint) {
        return requestSpecification
                .get(endpoint);
    }
}
