package se.taco.transport.delay.gtfs

import com.google.transit.realtime.GtfsRealtime
import khttp.async
import khttp.responses.Response
import kotlinx.coroutines.experimental.future.await
import mu.KotlinLogging
import se.taco.transport.delay.getEnv
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger { "GtfsService" }
private val ACCEPTED_RANGE = 200..399
private val GTFS_URL = getEnv("GTFS_URL", "https://gtfsr-pp.samtrafiken.se")
private val API_KEY = getEnv("API_KEY")

class GtfsService {

    suspend fun getServiceAlerts(operator: String): GtfsRealtime.FeedMessage {
        return sendRequest("$GTFS_URL/$operator/ServiceAlerts.pb?key=$API_KEY", GtfsRealtime.FeedMessage::parseFrom)
    }

    suspend fun getTripUpdate(operator: String): GtfsRealtime.FeedMessage {
        return sendRequest("$GTFS_URL/$operator/TripUpdates.pb?key=$API_KEY", GtfsRealtime.FeedMessage::parseFrom)
    }

    private suspend fun <T> sendRequest(url: String, block :(ByteArray) -> T ): T {
        val requestResult: CompletableFuture<Response> = CompletableFuture()
        val headers = mapOf("Content-Encoding" to "gzip")
        async.request(
                method = "GET",
                headers = headers,
                url = url,
                onError = {
                    logger.error("Request failed to $GTFS_URL")
                    requestResult.completeExceptionally(this)
                },
                onResponse = { requestResult.complete(this) },
                timeout = 45.0
        )
        val response = requestResult.await()
        if (response.statusCode in ACCEPTED_RANGE) {
            logger.error {
                "Error response from sverker, url: $GTFS_URL status code: ${response.statusCode}}."
            }
        }
        return block(response.content)
    }

}