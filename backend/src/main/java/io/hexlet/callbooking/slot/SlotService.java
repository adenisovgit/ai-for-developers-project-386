package io.hexlet.callbooking.slot;

import io.hexlet.callbooking.booking.BookingRecord;
import io.hexlet.callbooking.booking.BookingService;
import io.hexlet.callbooking.generated.model.OccupiedSlot;
import io.hexlet.callbooking.shared.error.BadRequestException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SlotService {

    private final BookingService bookingService;

    public SlotService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public List<OccupiedSlot> list(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate не может быть позже endDate.");
        }

        return bookingService.findByLocalDateRange(startDate, endDate).stream()
            .map(this::toOccupiedSlot)
            .toList();
    }

    private OccupiedSlot toOccupiedSlot(BookingRecord bookingRecord) {
        return new OccupiedSlot(
            OffsetDateTime.ofInstant(bookingRecord.startTime(), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(bookingRecord.endTime(), ZoneOffset.UTC)
        );
    }
}
