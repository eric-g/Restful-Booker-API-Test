package tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import models.request.AuthRequest;
import models.request.BookingDates;
import models.request.CreateBookingRequest;
import models.response.AuthResponse;
import models.response.Booking;
import models.response.BookingIdsResponse;
import models.response.CreateBookingResponse;
import services.AuthService;
import services.BookingService;

public class BookingServiceTest {

    private int bookingId;
    private String firstName = "JohnTest1";
    private String lastName = "Doe";

    @BeforeClass(alwaysRun = true)
    public void healthCheck(ITestContext context) {
        ITestNGMethod[] allMethods = context.getAllTestMethods();
        if(!HealthCheckTest.healthCheck()){
            throw new SkipException("Health check API is down!!");
        }
    }

    @Test(
        priority = 1,
        description = "Create a booking with valid data",
        groups = {"regression"}
    )
    public void createBooking() {
        
        CreateBookingRequest createBookingRequest = new CreateBookingRequest.Builder()
            .firstname(firstName)
            .lastname(lastName)
            .totalprice(100)
            .depositpaid(true)
            .bookingdates(new BookingDates.Builder()
                .checkin("2025-10-01")
                .checkout("2025-10-10")
                .build())
            .additionalneeds("Breakfast")
            .build();

        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.createBooking(createBookingRequest);
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        CreateBookingResponse createBookingResponse = bookingResponse.as(CreateBookingResponse.class);
        Assert.assertTrue(createBookingResponse.bookingid() !=0, "bookingid should not be null");
        bookingId = createBookingResponse.bookingid();
    }

    @Test(
        priority = 2,
        description = "Get all bookings and validate newly created booking ID is present",
        dependsOnMethods = {"createBooking"},
        groups = {"regression"}
    )
    public void getAllBookingIdsContainsCreatedBooking() {
         if (bookingId == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingByParam(null);
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        List<BookingIdsResponse> bookingServiceResponse = List.of(bookingResponse.as(BookingIdsResponse[].class));
        
        Assert.assertTrue(bookingServiceResponse.size() > 0, "Expected at least one booking, got none");
        Assert.assertTrue(bookingServiceResponse.get(0).bookingid() != 0, "bookingid should not be null");
        boolean bookingIdFound = bookingServiceResponse.stream()
            .anyMatch(booking -> booking.bookingid() == bookingId);
        Assert.assertTrue(bookingIdFound, "Expected booking ID " + bookingId + " to be present in the list of bookings");
    }

     @Test(
        priority = 3,
        description = "Retrieve newly created booking by ID",
        dependsOnMethods = "createBooking",
        groups = {"regression"}
    )
    public void getBookingById() {
        if (bookingId == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingById(bookingId);
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        Booking createBookingResponse = bookingResponse.as(Booking.class);
        Assert.assertEquals(createBookingResponse.firstname(), firstName, "Expected firstname " + firstName + ", but got " + createBookingResponse.firstname());
    }

    @Test(
        priority = 4,
        description = "Retrieve booking ID by first name",
        dependsOnMethods = "createBooking",
        groups = {"regression"}
    )
    public void getBookingIdByFirstName() {
        if (bookingId == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }
        
        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingByParam(new HashMap<>(Map.of("firstname", firstName)));
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        List<BookingIdsResponse> bookingServiceResponse = List.of(bookingResponse.as(BookingIdsResponse[].class));
        Assert.assertTrue(bookingServiceResponse.size() > 0, "Expected at least one booking, got none");
        boolean bookingIdFound = bookingServiceResponse.stream()
            .anyMatch(booking -> booking.bookingid() == bookingId);
        Assert.assertTrue(bookingIdFound, "Expected booking ID " + bookingId + " to be present in the list of bookings");
    }

    @Test(
        priority = 5,
        description = "Update (put) booking without required auth cookie",
        dependsOnMethods = "tests.BookingServiceTest.createBooking",
        groups = {"regression"}
    )
    public void updateBookingWithoutAuthCookie() {
        if (bookingId == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        BookingService bookingService = new BookingService();
        CreateBookingRequest updatedBookingRequest = new CreateBookingRequest.Builder()
            .firstname("Updated_" + firstName)
            .lastname("Updated_" + lastName)
            .totalprice(250)
            .depositpaid(true)
            .bookingdates(new BookingDates.Builder()
                .checkin("2025-12-01")
                .checkout("2025-12-10")
                .build())
            .additionalneeds("Dinner")
            .build();

        Response updatedBookingResponse = bookingService.updateBooking(updatedBookingRequest, bookingId);
        Assert.assertEquals(updatedBookingResponse.getStatusCode(), 403, "Expected status code 403, but got " + updatedBookingResponse.getStatusCode());
        String actualReason = updatedBookingResponse.getBody().asPrettyString();
        Assert.assertEquals(actualReason, "Forbidden", "Expected reason 'Forbidden', but got " + actualReason);

    }
     @Test(
        priority = 6,
        description = "Update (put) booking without required auth cookie",
        dependsOnMethods = "tests.BookingServiceTest.createBooking",
        groups = {"regression"}
    )
    public void testUpdateBookingWithAuthCookie() {
        if (bookingId == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        AuthService authService = new AuthService();
        AuthRequest authRequest = new AuthRequest(authService.getUsername(), authService.getPassword());
        Response authServiceResponse = authService.getTokenResponse(authRequest);
        Assert.assertEquals(authServiceResponse.getStatusCode(), 200, "Status code should be 200");
        
        AuthResponse authResponse = authServiceResponse.as(AuthResponse.class);
        String authToken = authResponse.token();

        BookingService bookingService = new BookingService();
        bookingService.setAuthToken(authToken);
        CreateBookingRequest updatedBookingRequest = new CreateBookingRequest.Builder()
            .firstname("Updated_" + firstName)
            .lastname("Updated_" + lastName)
            .totalprice(250)
            .depositpaid(true)
            .bookingdates(new BookingDates.Builder()
                .checkin("2025-12-01")
                .checkout("2025-12-10")
                .build())
            .additionalneeds("Dinner")
            .build();

        Response updatedBookingResponse = bookingService.updateBooking(updatedBookingRequest, bookingId);
        Assert.assertEquals(updatedBookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + updatedBookingResponse.getStatusCode());
        Booking updatedBooking = updatedBookingResponse.as(Booking.class);
        Assert.assertEquals(updatedBooking.lastname(), "Updated_" + lastName, "Expected updated lastname " + lastName + ", but got " + updatedBooking.lastname());
    }
    //Response bookingResponse = bookingService.getBookingId(new HashMap<>(Map.of("id", String.valueOf(bookingid))));
}
