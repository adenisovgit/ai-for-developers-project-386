import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using TypeSpec.Http;
using TypeSpec.Rest;

@service(#{
  title: "Call Booking Service API"
})
namespace CallBooking;

enum DurationMinutes {
  fifteen: 15,
  thirty: 30,
  fortyFive: 45,
  sixty: 60,
}

@pattern("^([01][0-9]|2[0-3]):00$", "Must be a whole-hour time in HH:mm format.")
scalar HourOnlyTime extends string;

@pattern("^#[0-9A-Fa-f]{6}$", "Must be a valid HEX color.")
scalar HexColor extends string;

model EventType {
  @format("uuid")
  id: string;
  title: string;
  description: string;
  durationMinutes: DurationMinutes;
  availableFrom: HourOnlyTime;
  availableTo: HourOnlyTime;
  color: HexColor;
}

model EventTypeCreateRequest {
  title: string;
  description: string;
  durationMinutes: DurationMinutes;
  availableFrom: HourOnlyTime;
  availableTo: HourOnlyTime;
  color: HexColor;
}

model OccupiedSlot {
  startTime: utcDateTime;
  endTime: utcDateTime;
}

model Booking {
  @format("uuid")
  id: string;
  @format("uuid")
  eventTypeId: string;
  startTime: utcDateTime;
  guestName: string;
  guestEmail: string;
  comment?: string;
}

model BookingRequest {
  @format("uuid")
  eventTypeId: string;
  startTime: utcDateTime;
  guestName: string;
  guestEmail: string;
  comment?: string;
}

@route("/event-types")
interface EventTypes {
  @opExample(
    #{
      returnType: #[
        #{
          id: "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
          title: "Знакомство",
          description: "15 минут на первый звонок и знакомство.",
          durationMinutes: DurationMinutes.fifteen,
          availableFrom: "09:00",
          availableTo: "18:00",
          color: "#3B82F6",
        },
        #{
          id: "45ce4c0d-4708-43f6-b588-c8af0181f3ea",
          title: "Техническое интервью",
          description: "Подробный технический созвон на 45 минут.",
          durationMinutes: DurationMinutes.fortyFive,
          availableFrom: "10:00",
          availableTo: "17:00",
          color: "#14B8A6",
        },
      ],
    }
  )
  @get list(): EventType[];

  @opExample(
    #{
      parameters: #{
        eventType: #{
          title: "Разбор резюме",
          description: "Созвон на 30 минут по вашему резюме.",
          durationMinutes: DurationMinutes.thirty,
          availableFrom: "09:00",
          availableTo: "18:00",
          color: "#22C55E",
        },
      },
      returnType: #{
        statusCode: 201,
        eventType: #{
          id: "b501d860-b0d1-4b8a-92c8-4597797b6e49",
          title: "Разбор резюме",
          description: "Созвон на 30 минут по вашему резюме.",
          durationMinutes: DurationMinutes.thirty,
          availableFrom: "09:00",
          availableTo: "18:00",
          color: "#22C55E",
        },
      },
    }
  )
  @post create(@body eventType: EventTypeCreateRequest): {
    @statusCode statusCode: 201;
    @body eventType: EventType;
  };
}

@route("/slots")
interface Slots {
  @opExample(
    #{
      parameters: #{
        startDate: plainDate.fromISO("2026-04-06"),
        endDate: plainDate.fromISO("2026-04-06"),
      },
      returnType: #[
        #{
          startTime: utcDateTime.fromISO("2026-04-06T09:30:00Z"),
          endTime: utcDateTime.fromISO("2026-04-06T10:00:00Z"),
        },
        #{
          startTime: utcDateTime.fromISO("2026-04-06T12:00:00Z"),
          endTime: utcDateTime.fromISO("2026-04-06T12:30:00Z"),
        },
      ],
    }
  )
  @get list(
    @query startDate: plainDate,
    @query endDate: plainDate
  ): OccupiedSlot[];
}

@route("/bookings")
interface Bookings {
  @opExample(
    #{
      parameters: #{
        booking: #{
          eventTypeId: "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
          startTime: utcDateTime.fromISO("2026-04-06T09:00:00Z"),
          guestName: "Анна Иванова",
          guestEmail: "anna@example.com",
          comment: "Хочу обсудить формат сотрудничества.",
        },
      },
      returnType: #{
        statusCode: 201,
        booking: #{
          id: "0d0c61e8-b1a7-4cff-a2d6-dde1e41445c1",
          eventTypeId: "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
          startTime: utcDateTime.fromISO("2026-04-06T09:00:00Z"),
          guestName: "Анна Иванова",
          guestEmail: "anna@example.com",
          comment: "Хочу обсудить формат сотрудничества.",
        },
      },
    }
  )
  @post create(@body booking: BookingRequest): {
    @statusCode statusCode: 201;
    @body booking: Booking;
  } | {
    @statusCode statusCode: 400 | 409;
    @body error: string;
  };

  @opExample(
    #{
      returnType: #[
        #{
          id: "0d0c61e8-b1a7-4cff-a2d6-dde1e41445c1",
          eventTypeId: "7f6f0c13-5a45-43ec-8eef-89fce35a4d01",
          startTime: utcDateTime.fromISO("2026-04-06T09:00:00Z"),
          guestName: "Анна Иванова",
          guestEmail: "anna@example.com",
          comment: "Хочу обсудить формат сотрудничества.",
        },
        #{
          id: "98b09a95-b9ce-4e06-b4ba-f4c9e7452c67",
          eventTypeId: "45ce4c0d-4708-43f6-b588-c8af0181f3ea",
          startTime: utcDateTime.fromISO("2026-04-07T11:00:00Z"),
          guestName: "Илья Петров",
          guestEmail: "ilya@example.com",
        },
      ],
    }
  )
  @get list(): Booking[];
}
