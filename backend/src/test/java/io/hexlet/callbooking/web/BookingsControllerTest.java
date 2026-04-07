package io.hexlet.callbooking.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.callbooking.booking.BookingService;
import io.hexlet.callbooking.booking.BookingsController;
import io.hexlet.callbooking.generated.model.Booking;
import io.hexlet.callbooking.shared.error.ApiExceptionHandler;
import io.hexlet.callbooking.shared.error.ConflictException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;

class BookingsControllerTest {

    private BookingService bookingService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingsController(bookingService))
            .setControllerAdvice(new ApiExceptionHandler())
            .setMessageConverters(
                new StringHttpMessageConverter(StandardCharsets.UTF_8),
                new MappingJackson2HttpMessageConverter(objectMapper)
            )
            .build();
    }

    @Test
    void returnsConflictAsPlainText() throws Exception {
        when(bookingService.create(any())).thenThrow(new ConflictException("Выбранный слот уже занят."));

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
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith("text/plain"))
            .andExpect(content().string("Выбранный слот уже занят."));
    }

    @Test
    void returnsCreatedBookingPayload() throws Exception {
        Booking booking = new Booking(
            UUID.fromString("0d0c61e8-b1a7-4cff-a2d6-dde1e41445c1"),
            UUID.fromString("7f6f0c13-5a45-43ec-8eef-89fce35a4d01"),
            OffsetDateTime.of(2026, 4, 6, 9, 0, 0, 0, ZoneOffset.UTC),
            "Анна Иванова",
            "anna@example.com"
        ).comment("Комментарий");

        when(bookingService.create(any())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                      "startTime": "2026-04-06T09:00:00Z",
                      "guestName": "Анна Иванова",
                      "guestEmail": "anna@example.com",
                      "comment": "Комментарий"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andExpect(content().json("""
                {
                  "id": "0d0c61e8-b1a7-4cff-a2d6-dde1e41445c1",
                  "eventTypeId": "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
                  "startTime": "2026-04-06T09:00:00Z",
                  "guestName": "Анна Иванова",
                  "guestEmail": "anna@example.com",
                  "comment": "Комментарий"
                }
                """));
    }
}
