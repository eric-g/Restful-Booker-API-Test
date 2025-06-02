# Restful Booker Automation Framework Example

For testing API: https://restful-booker.herokuapp.com/apidoc/index.html

A Maven-based Java/RestAssured test framework with Github workflow.

## Details

Test services extend from BaseService.

AuthService - obtain auth token for put/delete bookings

BookingService - create, update, retrieve and delete bookings

HealthCheckService - for API smoke test, basic operability check

Includes logging, authentication

## Operation

Clone repo

Add variables to .env: USERNAME, PASSWORD and logFilePath="log/restful-booker-log.log"

execute mvn test -DsuiteName={suiteName} (default is /src/main/resources/test_suites/testng.xml)
