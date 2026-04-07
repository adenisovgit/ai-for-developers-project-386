package io.hexlet.callbooking.booking;

import io.hexlet.callbooking.eventtype.EventTypeRecord;
import io.hexlet.callbooking.eventtype.EventTypeService;
import io.hexlet.callbooking.generated.model.Booking;
import io.hexlet.callbooking.generated.model.BookingRequest;
import io.hexlet.callbooking.shared.error.ConflictException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingRulesService bookingRulesService;
    private final EventTypeService eventTypeService;

    public BookingService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        BookingRulesService bookingRulesService,
        EventTypeService eventTypeService
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.bookingRulesService = bookingRulesService;
        this.eventTypeService = eventTypeService;
    }

    public List<Booking> list() {
        return bookingRepository.findAll().stream().map(bookingMapper::toModel).toList();
    }

    public synchronized Booking create(BookingRequest request) {
        EventTypeRecord eventType = eventTypeService.getRequiredRecord(request.getEventTypeId());
        OffsetDateTime endTime = bookingRulesService.validateAndResolveEndTime(request, eventType).toOffsetDateTime();

        if (bookingRepository.existsOverlapping(request.getStartTime().toInstant(), endTime.toInstant())) {
            throw new ConflictException("Выбранный слот уже занят.");
        }

        BookingRecord saved = bookingRepository.save(bookingMapper.toRecord(UUID.randomUUID(), request, endTime));
        return bookingMapper.toModel(saved);
    }

    public List<BookingRecord> findByLocalDateRange(LocalDate startDate, LocalDate endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return bookingRepository.findByStartDateRange(
            startDate.atStartOfDay(zoneId).toInstant(),
            endDate.plusDays(1).atStartOfDay(zoneId).toInstant()
        );
    }
}
