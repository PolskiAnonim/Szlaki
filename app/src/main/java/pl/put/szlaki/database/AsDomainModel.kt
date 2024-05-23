package pl.put.szlaki.database

import pl.put.szlaki.domain.Bounds
import pl.put.szlaki.domain.TrailPoint
import pl.put.szlaki.domain.TrailSegment
import pl.put.szlaki.domain.TrailWaypoint
import pl.put.szlaki.domain.TrailInList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//For trail list
@JvmName("asDomainModelTrail")
fun List<TrailEntity>.AsDomainModel(): List<TrailInList> {
    return map {
        TrailInList(id=it.id,it.name,it.length,
            Bounds(it.maxLatitude,it.maxLongitude,it.minLatitude,it.minLongitude),
            LocalDateTime.parse(it.timeStart,DateTimeFormatter.ISO_DATE_TIME),
            LocalDateTime.parse(it.timeEnd,DateTimeFormatter.ISO_DATE_TIME),
        )
    }
}

//Waypoints
@JvmName("asDomainModelWaypoint")
fun List<WaypointEntity>.AsDomainModel(): List<TrailWaypoint> {
    return map {
        TrailWaypoint(name = it.name,
            TrailPoint(id=it.waypointId,
                latitude = it.latitude,
                longitude = it.longitude,
                elevation = it.elevation,
                time = LocalDateTime.parse(it.time, DateTimeFormatter.ISO_DATE_TIME)
            )
        )
    }
}

//Segments
@JvmName("asDomainModelSegment")
fun List<SegmentEntity>.AsDomainModel(): List<TrailSegment> {
    return map {
        TrailSegment(id=it.segmentId, it.meanElevation,
            it.upElevation,it.downElevation,
            LocalDateTime.parse(it.timeStart, DateTimeFormatter.ISO_DATE_TIME),
            LocalDateTime.parse(it.timeEnd, DateTimeFormatter.ISO_DATE_TIME),
            it.length)
    }
}
