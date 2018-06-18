package se.taco.transport.delay.controller

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import se.taco.transport.delay.controller.routes.alerts
import se.taco.transport.delay.controller.routes.delay
import se.taco.transport.delay.gtfs.GtfsService

class Controller(
        private val gtfsService: GtfsService = GtfsService()
) {

    fun server(port: Int) = embeddedServer(Netty, port) { module() }

    private fun Application.module() {
        install(DefaultHeaders)
        install(Routing) {
            delay(gtfsService)
            alerts(gtfsService)
        }
    }
}


