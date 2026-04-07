package io.hexlet.callbooking.booking;

import io.hexlet.callbooking.generated.model.Booking;
import io.hexlet.callbooking.generated.model.BookingRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingRecord toRecord(UUID id, BookingRequest request, OffsetDateTime endTime) {
        return new BookingRecord(
            id,
            request.getEventTypeId(),
            request.getStartTime().toInstant(),
            endTime.toInstant(),
            request.getGuestName(),
            request.getGuestEmail(),
            request.getComment()
        );
    }

    public Booking toModel(BookingRecord record) {
        return new Booking(
            record.id(),
            record.eventTypeId(),
            OffsetDateTime.ofInstant(record.startTime(), ZoneOffset.UTC),
            record.guestName(),
            record.guestEmail()
        ).comment(record.comment());
    }
}
