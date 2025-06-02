package services;

import java.util.HashMap;

import io.restassured.response.Response;
import models.request.CreateBookingRequest;

public class BookingService extends BaseService {
            private String path = "/booking";

            public Response getBookingByParam(HashMap<String, String> queryParamMap) {
                addQueryParameter(queryParamMap);
                return get(path);
            }

            public Response getBookingById(int id) {
                path = path + "/" + id;
                return get(path);
            }

            public Response createBooking(CreateBookingRequest payload) {
                return post(payload, path);
            }
}
