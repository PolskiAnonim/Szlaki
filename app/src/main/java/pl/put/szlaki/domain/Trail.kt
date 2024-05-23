package pl.put.szlaki.domain

import java.time.LocalDateTime

data class Bounds(
    var maxLatitude: Float,
    var maxLongitude: Float,
    var minLatitude: Float,
    var minLongitude: Float
)

data class TrailInList(
    val id: Long,
    var name: String,
    var length: Double,
    var bounds: Bounds,
    var timeStart: LocalDateTime?,
    var timeEnd: LocalDateTime?
)


data class Trail(
    val id: Long,
    var name: String,
    var length: Double,
    var bounds: Bounds,
    var timeStart: LocalDateTime?,
    var timeEnd: LocalDateTime?,
    val waypoints: MutableList<TrailWaypoint>,
    val segments: MutableList<TrailSegment>
)

data class TrailWaypoint(
    var name: String,
    var point: TrailPoint
)

data class TrailPoint(
    val id: Long,
    var latitude: Float,
    var longitude: Float,
    var elevation: Float,
    var time: LocalDateTime?
)

data class TrailSegment(
    val id: Long,
    var meanElevation: Float,
    var upElevation: Float,
    var downElevation: Float,
    var timeStart: LocalDateTime?,
    var timeEnd: LocalDateTime?,
    var length: Double
)
