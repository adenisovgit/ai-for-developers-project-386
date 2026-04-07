package io.hexlet.callbooking.eventtype;

import io.hexlet.callbooking.generated.model.DurationMinutes;
import io.hexlet.callbooking.generated.model.EventType;
import io.hexlet.callbooking.generated.model.EventTypeCreateRequest;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EventTypeMapper {

    public EventTypeRecord toRecord(UUID id, EventTypeCreateRequest request) {
        return new EventTypeRecord(
            id,
            request.getTitle(),
            request.getDescription(),
            request.getDurationMinutes().getValue().intValueExact(),
            LocalTime.parse(request.getAvailableFrom()),
            LocalTime.parse(request.getAvailableTo()),
            request.getColor()
        );
    }

    public EventType toModel(EventTypeRecord record) {
        return new EventType(
            record.id(),
            record.title(),
            record.description(),
            DurationMinutes.fromValue(BigDecimal.valueOf(record.durationMinutes())),
            record.availableFrom().toString(),
            record.availableTo().toString(),
            record.color()
        );
    }
}
