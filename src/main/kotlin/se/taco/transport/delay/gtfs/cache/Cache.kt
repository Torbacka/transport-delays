package se.taco.transport.delay.gtfs.cache

import com.google.transit.realtime.GtfsRealtime
import se.taco.transport.delay.gtfs.MessageType

interface Cache {
    suspend fun cache(messageType: MessageType, operator: String, byteArray: ByteArray)
    suspend fun retrieve(messageType: MessageType, operator: String): GtfsRealtime.FeedMessage
}