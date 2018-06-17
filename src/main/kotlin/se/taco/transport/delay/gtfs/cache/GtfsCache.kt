package se.taco.transport.delay.gtfs.cache

import com.google.transit.realtime.GtfsRealtime
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.experimental.future.await
import se.taco.transport.delay.getEnv
import se.zensum.storage.Codec.StringToByteArrayCodec


private val REDIS_URL = getEnv("REDIS_URL", "redis://redis")
private val REDIS_PASSWORD = getEnv("REDIS_PASSWORD", "")
private const val REALTIME_KEY = "transport-delays"

class GtfsCache private constructor(private val redisCommands: RedisAsyncCommands<String, ByteArray>) : Cache {

    override suspend fun cache(cacheKey: CacheKey, byteArray: ByteArray) {
        redisCommands.hset(REALTIME_KEY, "${cacheKey.key}", byteArray).await()
    }

    override suspend fun retrieve(cacheKey: CacheKey): GtfsRealtime.FeedMessage {
        val byteArray = redisCommands.hget(REALTIME_KEY, "${cacheKey.key}").await()
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

enum class CacheKey(val key: String) {
    TRIP_KEY("trip-message"),
    ALERT_KEY("alert-message")
}