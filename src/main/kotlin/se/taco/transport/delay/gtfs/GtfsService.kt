package se.taco.transport.delay.gtfs

import mu.KotlinLogging
import se.taco.transport.delay.getEnv
import se.taco.transport.delay.gtfs.cache.Cache
import se.taco.transport.delay.gtfs.cache.CacheKey
import se.taco.transport.delay.gtfs.cache.GtfsCache

private val logger = KotlinLogging.logger { "GtfsService" }


class GtfsService(
        private val storage: Cache = GtfsCache.create()
) {

    suspend fun cacheServiceAlerts(operator: String) {
        val content = GtfsProxy.sendRequest("ServiceAlerts", operator)
        storage.cache(CacheKey.ALERT_KEY, content)
    }

    suspend fun cacheTripUpdate(operator: String) {
        val content = GtfsProxy.sendRequest("TripUpdates", operator)
        storage.cache(CacheKey.TRIP_KEY, content)
    }
}