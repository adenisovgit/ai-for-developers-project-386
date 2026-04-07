package io.hexlet.callbooking.booking;

import io.hexlet.callbooking.generated.api.BookingsApi;
import io.hexlet.callbooking.generated.model.Booking;
import io.hexlet.callbooking.generated.model.BookingRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingsController implements BookingsApi {

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ResponseEntity<Booking> bookingsCreate(@Valid BookingRequest bookingRequest) {
        return ResponseEntity.status(201).body(bookingService.create(bookingRequest));
    }

    @Override
    public ResponseEntity<List<Booking>> bookingsList() {
        return ResponseEntity.ok(bookingService.list());
    }
}
