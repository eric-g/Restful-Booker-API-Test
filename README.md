# Restful Booker Automation Framework Example

For testing API: https://restful-booker.herokuapp.com/apidoc/index.html

A Maven-based Java/RestAssured test framework with Github workflow.

## Details

src/main: Test services extend from BaseService.

    - AuthService - obtain auth token for put/delete bookings

    - BookingService - create, update, retrieve and delete bookings

    - HealthCheckService - for API smoke test, basic operability check

src/main/resources:
    
    - log4j2.xml logging configuration

src/main/resources/test_suites: TestNG suite .xml files by group

    - regression.xml: all tests

    - smoke.xml: only AuthTokenTest cases and HealthCheck

src/test:

    - Contains test cases

## Operation

    - Requires: Maven, Java 21

    - Clone repo

    - Add variables to .env: USERNAME, PASSWORD

execute `mvn test -DsuiteName={suiteName}` (default is /src/main/resources/test_suites/smoke.xml)
