package se.taco.transport.delay.controller

import com.googlecode.protobuf.format.JsonFormat
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import se.taco.transport.delay.gtfs.GtfsService
import se.taco.transport.delay.gtfs.MessageType

class DelayController(
        private val gtfsService: GtfsService = GtfsService()
) {

    fun server(port: Int) = embeddedServer(Netty, port, module = delay())

    private fun delay(): Application.() -> Unit = {
        routing {
            route("/delays") {
                get {
                    val feedMessage = gtfsService.retrieve(MessageType.TRIP)
                    call.respond(HttpStatusCode.OK,  JsonFormat().printToString(feedMessage))
                }
                post("/searches") {
                    call.respond(HttpStatusCode.OK, """{ "test": "ok" }""")
                }
            }
            route("/alerts") {
                get {
                    val feedMessage = gtfsService.retrieve(MessageType.ALERT)
                    call.respond(HttpStatusCode.OK, feedMessage)
                }
            }
        }
    }
}


