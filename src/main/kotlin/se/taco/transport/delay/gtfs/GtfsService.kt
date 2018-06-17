package se.taco.transport.delay.gtfs

import mu.KotlinLogging
import se.taco.transport.delay.gtfs.cache.Cache
import se.taco.transport.delay.gtfs.cache.GtfsCache

private val logger = KotlinLogging.logger { "GtfsService" }


class GtfsService(
        private val storage: Cache = GtfsCache.create()
) {

    suspend fun retrieve() {

    }

    suspend fun cacheServiceAlerts(operator: String) {
        val content = GtfsProxy.sendRequest("ServiceAlerts", operator)
        storage.cache(MessageType.ALERT, content)
    }

    suspend fun cacheTripUpdate(operator: String) {
        val content = GtfsProxy.sendRequest("TripUpdates", operator)
        storage.cache(MessageType.TRIP, content)
    }
}

enum class MessageType(val key: String) {
    TRIP("trip-message"),
    ALERT("alert-message")
}