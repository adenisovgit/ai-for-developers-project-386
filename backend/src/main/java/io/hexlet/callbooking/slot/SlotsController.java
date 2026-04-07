package io.hexlet.callbooking.slot;

import io.hexlet.callbooking.generated.api.SlotsApi;
import io.hexlet.callbooking.generated.model.OccupiedSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlotsController implements SlotsApi {

    private final SlotService slotService;

    public SlotsController(SlotService slotService) {
        this.slotService = slotService;
    }

    @Override
    public ResponseEntity<List<OccupiedSlot>> slotsList(LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(slotService.list(startDate, endDate));
    }
}
