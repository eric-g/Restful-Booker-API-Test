package tests;

import services.HealthCheckService;

public class HealthCheckTest {
    public static boolean healthCheck() {
        return new HealthCheckService().isHealthy();
    }
}
