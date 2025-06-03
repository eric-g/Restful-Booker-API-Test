package tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import models.request.BookingDates;
import models.request.CreateBookingRequest;
import models.response.Booking;
import models.response.BookingIdsResponse;
import models.response.CreateBookingResponse;
import services.BookingService;

public class BookingServiceTest {

    private int bookingid;
    private String firstName = "JohnTest1";
    private String lastName = "Doe";

    @BeforeClass(alwaysRun = true)
    public void healthCheck(){
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
        bookingid = createBookingResponse.bookingid();
    }

    @Test(
        priority = 2,
        description = "Get all bookings and validate newly created booking ID is present",
        dependsOnMethods = {"createBooking"},
        groups = {"regression"}
    )
    public void getAllBookingIdsContainsCreatedBooking() {
         if (bookingid == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingByParam(null);
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        List<BookingIdsResponse> bookingServiceResponse = List.of(bookingResponse.as(BookingIdsResponse[].class));
        
        Assert.assertTrue(bookingServiceResponse.size() > 0, "Expected at least one booking, got none");
        Assert.assertTrue(bookingServiceResponse.get(0).bookingid() != 0, "bookingid should not be null");
        boolean bookingIdFound = bookingServiceResponse.stream()
            .anyMatch(booking -> booking.bookingid() == bookingid);
        Assert.assertTrue(bookingIdFound, "Expected booking ID " + bookingid + " to be present in the list of bookings");
    }

     @Test(
        priority = 3,
        description = "Retrieve newly created booking by ID",
        dependsOnMethods = "createBooking",
        groups = {"regression"}
    )
    public void getBookingById() {
        if (bookingid == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }

        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingById(bookingid);
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
        if (bookingid == 0) {
            throw new SkipException("Booking ID is not set, skipping test");
        }
        
        BookingService bookingService = new BookingService();
        Response bookingResponse = bookingService.getBookingByParam(new HashMap<>(Map.of("firstname", firstName)));
        Assert.assertEquals(bookingResponse.getStatusCode(), 200, "Expected status code 200, but got " + bookingResponse.getStatusCode());
        List<BookingIdsResponse> bookingServiceResponse = List.of(bookingResponse.as(BookingIdsResponse[].class));
        Assert.assertTrue(bookingServiceResponse.size() > 0, "Expected at least one booking, got none");
        boolean bookingIdFound = bookingServiceResponse.stream()
            .anyMatch(booking -> booking.bookingid() == bookingid);
        Assert.assertTrue(bookingIdFound, "Expected booking ID " + bookingid + " to be present in the list of bookings");
    }
    //Response bookingResponse = bookingService.getBookingId(new HashMap<>(Map.of("id", String.valueOf(bookingid))));
}
