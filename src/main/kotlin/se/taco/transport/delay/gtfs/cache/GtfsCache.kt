package se.taco.transport.delay.gtfs

import se.taco.transport.delay.getEnv
import java.time.ZoneId

private val REDIS_URL = getEnv("REDIS_URL", "redis://redis")
private val REDIS_PASSWORD = getEnv("REDIS_PASSWORD", "")
private const val REDIS_KEY = "reference-id"
val SWEDEN_TIMEZONE = ZoneId.of("Europe/Stockholm")!!

class GtfsCache private constructor(private val redisCommands: RedisAsyncCommands<String, String>) : Storage {


}