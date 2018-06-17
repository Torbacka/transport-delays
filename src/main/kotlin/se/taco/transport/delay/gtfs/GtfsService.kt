package se.taco.transport.delay.gtfs

import com.google.transit.realtime.GtfsRealtime
import mu.KotlinLogging
import se.taco.transport.delay.gtfs.cache.Cache
import se.taco.transport.delay.gtfs.cache.GtfsCache

private val logger = KotlinLogging.logger { "GtfsService" }


class GtfsService(
        private val operator: String = "sl",
        private val storage: Cache = GtfsCache.create()
) {

    suspend fun retrieve(messageType: MessageType): GtfsRealtime.FeedMessage {
        return storage.retrieve(messageType, operator)
    }

    suspend fun cacheServiceAlerts() {
        val content = GtfsProxy.sendRequest("ServiceAlerts", operator)
        storage.cache(MessageType.ALERT, operator, content)
    }

    suspend fun cacheTripUpdate() {
        val content = GtfsProxy.sendRequest("TripUpdates", operator)
        storage.cache(MessageType.TRIP, operator, content)
    }
}

enum class MessageType(val key: String) {
    TRIP("trip-message"),
    ALERT("alert-message")
}