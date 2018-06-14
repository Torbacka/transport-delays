package se.taco.transport.delay.controller

import io.ktor.application.Application
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

object DelayController {

    fun server(port: Int) = embeddedServer(Netty, port, module = delay())

    private fun delay(): Application.() -> Unit = {
        routing {
            route("/delays") {
                get("/") {

                }
                post("/searches") {

                }
            }
        }
    }
}


