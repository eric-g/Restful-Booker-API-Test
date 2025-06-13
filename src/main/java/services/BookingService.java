package services;

import java.util.HashMap;

import io.restassured.response.Response;
import models.request.CreateBookingRequest;

public class BookingService extends BaseService {
            private String path = "/booking";
            private String authToken = "invalidToken";

            public BookingService() {
                super();
            }

            public void setAuthToken(String authToken) {
                this.authToken = authToken;
            }

            public Response getBookingByParam(HashMap<String, String> queryParamMap) {
                addQueryParameter(queryParamMap);
                return get(path);
            }

            public Response getBookingById(int id) {
                String path = this.path + "/" + id;
                return get(path);
            }

            public Response createBooking(CreateBookingRequest payload) {
                return post(payload, path);
            }

            public Response updateBooking(CreateBookingRequest payload, int id) {
                String path = this.path + "/" + id;
                return put(payload, this.authToken, path);
            }
}
