package se.taco.transport.delay.controller.model

import java.time.LocalDateTime

data class Search(
        val data: LocalDateTime
)

data class TripSearch(
        val tripId: String
)