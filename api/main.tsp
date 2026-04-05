import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using TypeSpec.Http;
using TypeSpec.Rest;

@service({
  title: "Call Booking Service API",
  version: "1.0.0"
})
namespace CallBooking;

// Заглушка. Детальное проектирование API будет на Шаге 2.
@route("/ping")
op ping(): string;
