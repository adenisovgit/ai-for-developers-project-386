package io.hexlet.callbooking.integration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BackendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-04-01T00:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Test
    void returnsSeededEventTypesAfterStartup() throws Exception {
        mockMvc.perform(get("/event-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("7f6f0c13-5a45-43ec-8eef-89fce35a4d01"))
            .andExpect(jsonPath("$[1].id").value("45ce4c0d-4708-43f6-b588-c8af0181f3ea"));
    }

    @Test
    void createsEventTypeAndListsIt() throws Exception {
        mockMvc.perform(post("/event-types")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "title": "Разбор резюме",
                      "description": "Созвон на 30 минут по вашему резюме.",
                      "durationMinutes": 30,
                      "availableFrom": "09:00",
                      "availableTo": "18:00",
                      "color": "#22C55E"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Разбор резюме"));

        mockMvc.perform(get("/event-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void createsBookingListsBookingsAndReturnsOccupiedSlots() throws Exception {
        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                      "startTime": "2026-04-06T09:00:00Z",
                      "guestName": "Анна Иванова",
                      "guestEmail": "anna@example.com",
                      "comment": "Хочу обсудить формат сотрудничества."
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty());

        mockMvc.perform(get("/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].guestEmail").value("anna@example.com"));

        mockMvc.perform(get("/slots")
                .queryParam("startDate", "2026-04-06")
                .queryParam("endDate", "2026-04-06"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].startTime").value("2026-04-06T09:00:00Z"))
            .andExpect(jsonPath("$[0].endTime").value("2026-04-06T09:15:00Z"));
    }

    @Test
    void rejectsUnknownConflictingAndOffGridBookings() throws Exception {
        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "00000000-0000-0000-0000-000000000000",
                      "startTime": "2026-04-06T09:00:00Z",
                      "guestName": "Анна Иванова",
                      "guestEmail": "anna@example.com"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("text/plain"));

        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                      "startTime": "2026-04-06T09:00:00Z",
                      "guestName": "Анна Иванова",
                      "guestEmail": "anna@example.com"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                      "startTime": "2026-04-06T09:00:00Z",
                      "guestName": "Илья Петров",
                      "guestEmail": "ilya@example.com"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(content().string("Выбранный слот уже занят."));

        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                      "startTime": "2026-04-06T09:07:00Z",
                      "guestName": "Анна Иванова",
                      "guestEmail": "anna@example.com"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void servesSpaEntrypoints() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));

        mockMvc.perform(get("/admin"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));

        mockMvc.perform(get("/index.html"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Test SPA shell")));
    }

    @Test
    void servesStaticAssets() throws Exception {
        mockMvc.perform(get("/assets/test.js"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("window.__testAssetLoaded = true;")));
    }
}
