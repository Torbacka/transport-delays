package se.taco.transport.delay.controller.routes

import com.googlecode.protobuf.format.JsonFormat
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.*
import se.taco.transport.delay.controller.model.TripSearch
import se.taco.transport.delay.gtfs.GtfsService
import se.taco.transport.delay.gtfs.MessageType

fun Route.delay(gtfsService: GtfsService) {
    route("/delays") {
        get {
            val feedMessage = gtfsService.retrieve(MessageType.TRIP)
            call.respond(HttpStatusCode.OK, JsonFormat().printToString(feedMessage))
        }
        post("/searches") {
            val tripSearch: TripSearch = call.receive()

            call.respond(HttpStatusCode.OK, """{ "test": "ok" }""")
        }
    }

}