package filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class LoggingFilter implements Filter {

    static String className = Thread.currentThread().getStackTrace()[3].getClassName();
    private final Logger logger = LogManager.getLogger(className);

    public Response filter(FilterableRequestSpecification requestSpecification,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext ctx) {
        // Log the request and response details
        logRequest(requestSpecification);
        Response response = ctx.next(requestSpecification, responseSpecification);
        logResponse(response);
        return response;
    }

    public void logRequest(FilterableRequestSpecification requestSpecification) {
        logger.info("Request URI: " + requestSpecification.getURI());
        logger.info("Request Method: " + requestSpecification.getMethod());
        logger.info("Request Headers: " + requestSpecification.getHeaders());
        logger.info("Request Query Parameters: " + requestSpecification.getQueryParams());
        logger.info("Request Body: " + requestSpecification.getBody());
        logger.info("------------------------");
    }
    public void logResponse(Response response) {
        logger.info("Response Status Code: " + response.getStatusCode());
        logger.info("Response Headers: " + response.getHeaders());
        logger.info("------------------------");
        //logger.info("Response Body: " + response.asPrettyString());
    }

}
