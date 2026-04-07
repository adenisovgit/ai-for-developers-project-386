package io.hexlet.callbooking.eventtype;

import io.hexlet.callbooking.generated.api.EventTypesApi;
import io.hexlet.callbooking.generated.model.EventType;
import io.hexlet.callbooking.generated.model.EventTypeCreateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventTypesController implements EventTypesApi {

    private final EventTypeService eventTypeService;

    public EventTypesController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    @Override
    public ResponseEntity<EventType> eventTypesCreate(@Valid EventTypeCreateRequest eventTypeCreateRequest) {
        return ResponseEntity.status(201).body(eventTypeService.create(eventTypeCreateRequest));
    }

    @Override
    public ResponseEntity<List<EventType>> eventTypesList() {
        return ResponseEntity.ok(eventTypeService.list());
    }
}
