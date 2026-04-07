package io.hexlet.callbooking.eventtype;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryEventTypeRepository implements EventTypeRepository {

    private final Map<UUID, EventTypeRecord> storage = new LinkedHashMap<>();

    @Override
    public synchronized List<EventTypeRecord> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized Optional<EventTypeRecord> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized EventTypeRecord save(EventTypeRecord eventType) {
        storage.put(eventType.id(), eventType);
        return eventType;
    }
}
