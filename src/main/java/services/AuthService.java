package services;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.response.Response;
import models.request.AuthRequest;

public class AuthService extends BaseService {
    private String path = "/auth";
    private String username;
    private String password;
    private String token;

    public AuthService() {
        Dotenv dotenv = Dotenv.load();

        this.username = dotenv.get("USERNAME");
        this.password = dotenv.get("PASSWORD");
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
