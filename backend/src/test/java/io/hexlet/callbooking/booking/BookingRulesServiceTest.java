package io.hexlet.callbooking.booking;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.hexlet.callbooking.eventtype.EventTypeRecord;
import io.hexlet.callbooking.generated.model.BookingRequest;
import io.hexlet.callbooking.shared.error.BadRequestException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookingRulesServiceTest {

    private final BookingRulesService bookingRulesService = new BookingRulesService();

    @Test
    void resolvesEndTimeForSlotOnGrid() {
        BookingRequest request = new BookingRequest(
            UUID.randomUUID(),
            toZonedDateTime(LocalDate.of(2026, 4, 6), LocalTime.of(9, 30)).toOffsetDateTime(),
            "Анна Иванова",
            "anna@example.com"
        );

        ZonedDateTime endTime = assertDoesNotThrow(() ->
            bookingRulesService.validateAndResolveEndTime(request, sampleEventType(30))
        );

        assertEquals(LocalTime.of(10, 0), endTime.toLocalTime());
    }

    @Test
    void rejectsSlotOutsideAvailabilityWindow() {
        BookingRequest request = new BookingRequest(
            UUID.randomUUID(),
            toZonedDateTime(LocalDate.of(2026, 4, 6), LocalTime.of(17, 45)).toOffsetDateTime(),
            "Анна Иванова",
            "anna@example.com"
        );

        assertThrows(BadRequestException.class, () ->
            bookingRulesService.validateAndResolveEndTime(request, sampleEventType(30))
        );
    }

    @Test
    void rejectsSlotOutsideDurationGrid() {
        BookingRequest request = new BookingRequest(
            UUID.randomUUID(),
            toZonedDateTime(LocalDate.of(2026, 4, 6), LocalTime.of(9, 20)).toOffsetDateTime(),
            "Анна Иванова",
            "anna@example.com"
        );

        assertThrows(BadRequestException.class, () ->
            bookingRulesService.validateAndResolveEndTime(request, sampleEventType(15))
        );
    }

    private EventTypeRecord sampleEventType(int durationMinutes) {
        return new EventTypeRecord(
            UUID.randomUUID(),
            "Знакомство",
            "Тестовый звонок",
            durationMinutes,
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            "#3B82F6"
        );
    }

    private ZonedDateTime toZonedDateTime(LocalDate date, LocalTime time) {
        return date.atTime(time).atZone(ZoneId.systemDefault());
    }
}
