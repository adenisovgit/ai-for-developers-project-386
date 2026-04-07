package io.hexlet.callbooking.eventtype;

import java.time.LocalTime;
import java.util.UUID;

public record EventTypeRecord(
    UUID id,
    String title,
    String description,
    int durationMinutes,
    LocalTime availableFrom,
    LocalTime availableTo,
    String color
) {
}
