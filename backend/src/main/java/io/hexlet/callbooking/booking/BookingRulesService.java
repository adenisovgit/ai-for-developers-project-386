package io.hexlet.callbooking.booking;

import io.hexlet.callbooking.eventtype.EventTypeRecord;
import io.hexlet.callbooking.generated.model.BookingRequest;
import io.hexlet.callbooking.shared.error.BadRequestException;
import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class BookingRulesService {

    private final Clock clock;

    public BookingRulesService(Clock clock) {
        this.clock = clock;
    }

    public ZonedDateTime validateAndResolveEndTime(BookingRequest request, EventTypeRecord eventType) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startTime = request.getStartTime().atZoneSameInstant(zoneId);
        ZonedDateTime now = ZonedDateTime.ofInstant(clock.instant(), zoneId);
        int durationMinutes = eventType.durationMinutes();
        LocalTime localStart = startTime.toLocalTime();
        ZonedDateTime endTime = startTime.plusMinutes(durationMinutes);
        LocalTime localEnd = endTime.toLocalTime();

        if (startTime.getSecond() != 0 || startTime.getNano() != 0) {
            throw new BadRequestException("Время начала должно быть привязано к минутной сетке.");
        }

        if (!startTime.isAfter(now)) {
            throw new BadRequestException("Нельзя бронировать слоты в прошлом.");
        }

        if (localStart.isBefore(eventType.availableFrom()) || localEnd.isAfter(eventType.availableTo())) {
            throw new BadRequestException("Выбранное время выходит за окно доступности события.");
        }

        long minutesFromOpen = ChronoUnit.MINUTES.between(eventType.availableFrom(), localStart);
        if (minutesFromOpen < 0 || minutesFromOpen % durationMinutes != 0) {
            throw new BadRequestException("Время начала должно совпадать с сеткой слотов для выбранного события.");
        }

        return endTime;
    }
}
