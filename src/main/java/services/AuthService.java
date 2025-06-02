package services;

import io.restassured.response.Response;
import models.request.AuthRequest;

public class AuthService extends BaseService {
    private String path = "/auth";
    private String username;
    private String password;
    private String token;

    public AuthService() {

        this.username = System.getProperty("USERNAME");
        this.password = System.getProperty("PASSWORD");
    }

    public Response getTokenResponse(AuthRequest request) {
        return post(request, path);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
