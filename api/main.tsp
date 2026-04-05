import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using TypeSpec.Http;
using TypeSpec.Rest;

@service(#{
  title: "Call Booking Service API"
})
namespace CallBooking;

model EventType {
  @format("uuid")
  id: string;
  title: string;
  description: string;
  durationMinutes: int32;
  availableFrom: plainTime;
  availableTo: plainTime;
}

model Slot {
  startTime: utcDateTime;
}

model Booking {
  @format("uuid")
  id: string;
  @format("uuid")
  eventTypeId: string;
  startTime: utcDateTime;
  guestName: string;
  guestEmail: string;
  comment: string;
}

model BookingRequest {
  @format("uuid")
  eventTypeId: string;
  startTime: utcDateTime;
  guestName: string;
  guestEmail: string;
  comment: string;
}

@route("/event-types")
interface EventTypes {
  @get list(): EventType[];
  @post create(@body eventType: EventType): {
    @statusCode statusCode: 201;
    @body eventType: EventType;
  };
}

@route("/slots")
interface Slots {
  @get list(
    @query eventTypeId: string,
    @query startDate?: plainDate,
    @query endDate?: plainDate
  ): Slot[];
}

@route("/bookings")
interface Bookings {
  @post create(@body booking: BookingRequest): {
    @statusCode statusCode: 201;
    @body booking: Booking;
  } | {
    @statusCode statusCode: 400 | 409;
    @body error: string;
  };

  @get list(): Booking[];
}
