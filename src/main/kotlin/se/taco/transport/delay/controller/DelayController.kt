package se.taco.transport.delay.controller

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import khttp.get
import se.taco.transport.delay.gtfs.cache.Cache
import se.taco.transport.delay.gtfs.cache.CacheKey
import se.taco.transport.delay.gtfs.cache.GtfsCache

class DelayController(
        private val cache: Cache = GtfsCache.create()
) {

    fun server(port: Int) = embeddedServer(Netty, port, module = delay())

    private fun delay(): Application.() -> Unit = {
        routing {
            route("/delays") {
                get {
                    val feedMessage = cache.retrieve(CacheKey.TRIP_KEY)
                    call.respond(HttpStatusCode.OK, feedMessage)
                }
                post("/searches") {

                }
            }
            route("/alerts") {
                get{

                }
            }
        }
    }
}


