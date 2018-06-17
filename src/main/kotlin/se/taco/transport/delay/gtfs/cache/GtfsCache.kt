package se.taco.transport.delay.gtfs.cache

import com.google.transit.realtime.GtfsRealtime
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.experimental.future.await
import se.taco.transport.delay.getEnv
import se.taco.transport.delay.gtfs.MessageType
import se.zensum.storage.Codec.StringToByteArrayCodec


private val REDIS_URL = getEnv("REDIS_URL", "redis://redis")
private val REDIS_PASSWORD = getEnv("REDIS_PASSWORD", "")
private const val REALTIME_KEY = "transport-delays"

class GtfsCache private constructor(private val redisCommands: RedisAsyncCommands<String, ByteArray>) : Cache {

    override suspend fun cache(messageType: MessageType, byteArray: ByteArray) {
        redisCommands.hset(REALTIME_KEY, messageType.key, byteArray).await()
    }

    override suspend fun retrieve(messageType: MessageType): GtfsRealtime.FeedMessage {
        val byteArray = redisCommands.hget(REALTIME_KEY, messageType.key).await()
        return GtfsRealtime.FeedMessage.parseFrom(byteArray)

    }
    companion object {
        fun create(): GtfsCache {
            val redisURI = RedisURI.create(REDIS_URL)!!.apply { if (REDIS_PASSWORD != "") setPassword(REDIS_PASSWORD) }
            val client = RedisClient.create(redisURI)
                    .connect(StringToByteArrayCodec())
                    .async()!!
            return GtfsCache(client)
        }
    }
}