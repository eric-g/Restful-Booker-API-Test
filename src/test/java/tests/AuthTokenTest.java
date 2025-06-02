package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import models.request.AuthRequest;
import models.response.AuthResponse;
import services.AuthService;

public class AuthTokenTest extends BaseTest {

    AuthService authService = new AuthService();

    @BeforeClass
    public void healthCheck(){
        if(!HealthCheckTest.healthCheck()){
            throw new SkipException("Health check API is down! Skipping tests.");
        }
    }

    @Test(
        description = "Create Auth Token with valid username and password",
        groups = {"smoke", "regression"})
    public void testCreateAuthTokenWithValidCredentials() {
        AuthRequest authRequest = new AuthRequest(authService.getUsername(), authService.getPassword());
        Response authServiceResponse = authService.getTokenResponse(authRequest);
        Assert.assertEquals(authServiceResponse.getStatusCode(), 200, "Status code should be 200");
        
        AuthResponse authResponse = authServiceResponse.as(AuthResponse.class);
        Assert.assertTrue(authResponse.token() != null, "Token should not be null");
        Assert.assertTrue(authResponse.token().length() > 0, "Token should not be empty");
    }

    @Test(
        description = "Attempt to create Auth Token with invalid username and password results in error",
        groups = {"smoke", "regression"}
        )
    public void testCreateAuthTokenWithInvalidCredentials() {
        AuthRequest authRequest = new AuthRequest("invalidUser", "invalidPassword");
        Response invalidAuthServiceResponse = authService.getTokenResponse(authRequest);
        Assert.assertEquals(invalidAuthServiceResponse.getStatusCode(), 200, "Status code should be 200");
        
        String actualReason = toJson(invalidAuthServiceResponse).getString("reason");
        Assert.assertEquals(actualReason,"Bad credentials","Bad credentials should be displayed on providing incorrect credentials");
    }
}
