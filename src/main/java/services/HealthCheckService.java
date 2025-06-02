package services;

import io.restassured.response.Response;

public class HealthCheckService extends BaseService {
    private String path = "/ping";

    public boolean isHealthy() {
        Response healthCheckResponse = get(path);
        return healthCheckResponse.getStatusCode() == 201;
    }
}
