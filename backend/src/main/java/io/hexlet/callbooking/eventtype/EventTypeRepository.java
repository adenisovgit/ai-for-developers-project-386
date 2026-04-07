package io.hexlet.callbooking.eventtype;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventTypeRepository {

    List<EventTypeRecord> findAll();

    Optional<EventTypeRecord> findById(UUID id);

    EventTypeRecord save(EventTypeRecord eventType);
}
