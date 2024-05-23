package pl.put.szlaki.database


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//Trails
@Entity(tableName = "trails", indices = [Index(value = ["id"], unique = true),
    Index(value = ["name"], unique = true)])
data class TrailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val length: Double,
    val maxLatitude: Float,
    val maxLongitude: Float,
    val minLatitude: Float,
    val minLongitude: Float,
    val timeStart: String?,
    val timeEnd: String?
)

@Entity(tableName = "waypoints",
    foreignKeys = [
        ForeignKey(
            entity = TrailEntity::class,
            parentColumns = ["id"],
            childColumns = ["trailId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    primaryKeys = ["trailId","waypointId"],
    indices = [Index(value = ["trailId","waypointId"])])
data class WaypointEntity(
    val trailId: Long,
    val waypointId: Long,
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val elevation: Float,
    val time: String?
)

@Entity(tableName = "segments",
    foreignKeys = [
        ForeignKey(
            entity = TrailEntity::class,
            parentColumns = ["id"],
            childColumns = ["trailId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    primaryKeys = ["trailId","segmentId"],
    indices = [Index(value = ["trailId","segmentId"])])
data class SegmentEntity(
    val trailId:Long,
    val segmentId:Long,
    val meanElevation: Float,
    val upElevation: Float,
    val downElevation: Float,
    val timeStart: String?,
    val timeEnd: String?,
    val length: Double
)

@Entity(tableName = "timers",
    foreignKeys = [
        ForeignKey(
            entity = TrailEntity::class,
            parentColumns = ["id"],
            childColumns = ["trailId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    primaryKeys = ["trailId","date"],
    indices = [Index(value = ["trailId","date"])])
data class TimerEntity (
    val trailId:Long,
    val date: String,
    val time:String
)