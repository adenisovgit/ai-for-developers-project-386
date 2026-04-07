package io.hexlet.callbooking.eventtype;

import io.hexlet.callbooking.generated.model.EventType;
import io.hexlet.callbooking.generated.model.EventTypeCreateRequest;
import io.hexlet.callbooking.shared.error.BadRequestException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;

    public EventTypeService(EventTypeRepository eventTypeRepository, EventTypeMapper eventTypeMapper) {
        this.eventTypeRepository = eventTypeRepository;
        this.eventTypeMapper = eventTypeMapper;
    }

    public List<EventType> list() {
        return eventTypeRepository.findAll().stream().map(eventTypeMapper::toModel).toList();
    }

    public EventType create(EventTypeCreateRequest request) {
        validateAvailabilityRange(request.getAvailableFrom(), request.getAvailableTo());
        EventTypeRecord saved = eventTypeRepository.save(eventTypeMapper.toRecord(UUID.randomUUID(), request));
        return eventTypeMapper.toModel(saved);
    }

    public EventTypeRecord getRequiredRecord(UUID id) {
        return eventTypeRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Тип события не найден."));
    }

    public void seed(EventTypeRecord eventType) {
        eventTypeRepository.save(eventType);
    }

    private void validateAvailabilityRange(String availableFrom, String availableTo) {
        if (availableFrom.compareTo(availableTo) >= 0) {
            throw new BadRequestException("availableFrom должен быть меньше availableTo.");
        }
    }
}
