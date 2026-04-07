package io.hexlet.callbooking.booking;

import java.time.Instant;
import java.util.List;

public interface BookingRepository {

    List<BookingRecord> findAll();

    List<BookingRecord> findByStartDateRange(Instant rangeStart, Instant rangeEndExclusive);

    boolean existsOverlapping(Instant startTime, Instant endTime);

    BookingRecord save(BookingRecord bookingRecord);
}
