package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BaseTest {
    // Base test class for setting up common test configurations
    // This can include setting up the test environment, initializing variables, etc.
    // For example, you might want to set up a base URL or authentication token here.


    public void setup() {   
        // Common setup code for all tests
        // This could include initializing the auth token, setting up test data, etc.
    }
    public void teardown() {
        // Common teardown code for all tests
        // This could include cleaning up test data, closing connections, etc.
    }

    public static JsonPath toJson(Response response){
        return new JsonPath(response.asPrettyString());
    }


}
