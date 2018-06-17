package se.taco.transport.delay.gtfs.cache

import com.google.transit.realtime.GtfsRealtime

interface Cache {
    suspend fun cache(cacheKey: CacheKey, byteArray: ByteArray)
    suspend fun retrieve(cacheKey: CacheKey): GtfsRealtime.FeedMessage
}