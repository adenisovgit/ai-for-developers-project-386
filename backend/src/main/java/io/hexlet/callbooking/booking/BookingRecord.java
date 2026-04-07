package io.hexlet.callbooking.booking;

import java.time.Instant;
import java.util.UUID;

public record BookingRecord(
    UUID id,
    UUID eventTypeId,
    Instant startTime,
    Instant endTime,
    String guestName,
    String guestEmail,
    String comment
) {
}
