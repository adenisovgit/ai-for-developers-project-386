package io.hexlet.callbooking.booking;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final List<BookingRecord> storage = new ArrayList<>();

    @Override
    public synchronized List<BookingRecord> findAll() {
        return storage.stream()
            .sorted(Comparator.comparing(BookingRecord::startTime))
            .toList();
    }

    @Override
    public synchronized List<BookingRecord> findByStartDateRange(Instant rangeStart, Instant rangeEndExclusive) {
        return storage.stream()
            .filter(booking -> !booking.startTime().isBefore(rangeStart) && booking.startTime().isBefore(rangeEndExclusive))
            .sorted(Comparator.comparing(BookingRecord::startTime))
            .toList();
    }

    @Override
    public synchronized boolean existsOverlapping(Instant startTime, Instant endTime) {
        return storage.stream().anyMatch(booking ->
            startTime.isBefore(booking.endTime()) && endTime.isAfter(booking.startTime())
        );
    }

    @Override
    public synchronized BookingRecord save(BookingRecord bookingRecord) {
        storage.add(bookingRecord);
        return bookingRecord;
    }
}
