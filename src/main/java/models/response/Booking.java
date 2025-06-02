package models.response;

import models.request.BookingDates;

public record Booking(
    String firstname,
    String lastname,
    double totalprice,
    boolean depositpaid,
    BookingDates bookingdates,
    String additionalneeds) {}
