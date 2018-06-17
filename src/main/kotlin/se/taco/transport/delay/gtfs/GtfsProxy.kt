package se.taco.transport.delay.gtfs

import khttp.async
import khttp.responses.Response
import kotlinx.coroutines.experimental.future.await
import mu.KotlinLogging
import se.taco.transport.delay.getEnv
import java.util.concurrent.CompletableFuture

private val ACCEPTED_RANGE = 200..399
private val logger = KotlinLogging.logger { "GtfsProxy" }
private val GTFS_URL = getEnv("GTFS_URL", "https://gtfsr-pp.samtrafiken.se")
private val API_KEY = getEnv("API_KEY")

object GtfsProxy {
    suspend fun sendRequest(endpoint: String, operator: String, format: String = "pb"): ByteArray {
        val url = "$GTFS_URL/$operator/$endpoint.$format?key=$API_KEY"
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
        return response.content
    }
}