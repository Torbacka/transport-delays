package se.taco.transport.delay.gtfs.cache

import com.google.transit.realtime.GtfsRealtime
import se.taco.transport.delay.gtfs.MessageType

interface Cache {
    suspend fun cache(messageType: MessageType, byteArray: ByteArray)
    suspend fun retrieve(messageType: MessageType): GtfsRealtime.FeedMessage
}