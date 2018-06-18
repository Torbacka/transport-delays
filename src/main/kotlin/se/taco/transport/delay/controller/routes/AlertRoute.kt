package se.taco.transport.delay.controller.routes

import com.googlecode.protobuf.format.JsonFormat
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import se.taco.transport.delay.gtfs.GtfsService
import se.taco.transport.delay.gtfs.MessageType

fun Route.alerts(gtfsService: GtfsService) {
    route("/alerts") {
        get {
            val feedMessage = gtfsService.retrieve(MessageType.ALERT)
            call.response.header("ContentType", "application/json")
            call.respond(HttpStatusCode.OK, JsonFormat().printToString(feedMessage))
        }
    }
}