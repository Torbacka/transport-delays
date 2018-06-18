package se.taco.transport.delay

import kotlinx.coroutines.experimental.launch
import se.taco.transport.delay.controller.Controller
import se.taco.transport.delay.gtfs.GtfsService
import java.util.*
import kotlin.concurrent.schedule

fun main(args: Array<String>) {
    startTripUpdate()
    startServiceAlerts()
    Controller().server(50001).start()
}

private fun startServiceAlerts() {
    Timer().schedule(300, 10000) {
        launch {
            GtfsService().cacheServiceAlerts()
        }
    }
}

private fun startTripUpdate() {
    Timer().schedule(300, 10000) {
        launch {
            GtfsService().cacheTripUpdate()
        }
    }
}


fun getEnv(e: String, default: String? = null): String = System.getenv()[e] ?: default
?: throw RuntimeException("Missing environment variable $e and no default value is given.")