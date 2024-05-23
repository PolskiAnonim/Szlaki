package pl.put.szlaki.domain

import pl.put.szlaki.database.SegmentEntity
import pl.put.szlaki.database.TrailEntity
import pl.put.szlaki.database.WaypointEntity

fun Trail.AsDatabaseModel(): TrailEntity {
    return TrailEntity(this.id,this.name,this.length,
        this.bounds.maxLatitude,this.bounds.maxLongitude,
        this.bounds.minLatitude,this.bounds.minLongitude,
        this.timeStart.toString(),this.timeEnd.toString())
}

@JvmName("asDatabaseModelSegment")
fun MutableList<TrailSegment>.AsDatabaseModel(trailId: Long): List<SegmentEntity> {
    return map { SegmentEntity(trailId, it.id,it.meanElevation,it.upElevation,
        it.downElevation,it.timeStart.toString(),it.timeEnd.toString() ,it.length) }
}

@JvmName("asDatabaseModelWaypoint")
fun MutableList<TrailWaypoint>.AsDatabaseModel(id: Long): List<WaypointEntity> {
    return map { WaypointEntity(id,it.point.id,it.name,it.point.latitude,it.point.longitude,
        it.point.elevation, it.point.time.toString()
    ) }
}
