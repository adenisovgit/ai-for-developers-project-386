package io.hexlet.callbooking.eventtype;

import io.hexlet.callbooking.shared.config.AppProperties;
import java.time.LocalTime;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoEventTypeSeeder implements ApplicationRunner {

    private final EventTypeService eventTypeService;
    private final AppProperties appProperties;

    public DemoEventTypeSeeder(EventTypeService eventTypeService, AppProperties appProperties) {
        this.eventTypeService = eventTypeService;
        this.appProperties = appProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appProperties.getSeed().isEnabled()) {
            return;
        }

        eventTypeService.seed(new EventTypeRecord(
            UUID.fromString("7f6f0c13-5a45-43ec-8eef-89fce35a4d01"),
            "Знакомство",
            "15 минут на первый звонок и знакомство.",
            15,
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            "#3B82F6"
        ));

        eventTypeService.seed(new EventTypeRecord(
            UUID.fromString("45ce4c0d-4708-43f6-b588-c8af0181f3ea"),
            "Техническое интервью",
            "Подробный технический созвон на 45 минут.",
            45,
            LocalTime.of(10, 0),
            LocalTime.of(17, 0),
            "#14B8A6"
        ));
    }
}
