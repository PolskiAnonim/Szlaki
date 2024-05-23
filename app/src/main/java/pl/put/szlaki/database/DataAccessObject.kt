package pl.put.szlaki.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction

@Dao
interface TrailDao {
    //Times
    @Query("SELECT * FROM timers WHERE trailId=:id")
    suspend fun TimerGetList(id:Long): List<TimerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun TimerInsert(timer: TimerEntity)

    @Delete
    suspend fun TimerDelete(timer: TimerEntity)

    @Query("DELETE FROM timers")
    suspend fun TimerDeleteAll()

    //Trails
    @Query("SELECT * FROM trails")
    suspend fun TrailGetList(): List<TrailEntity>

    @Query("SELECT * FROM waypoints WHERE trailId=:trailId")
    suspend fun WaypointGetList(trailId: Long) : List<WaypointEntity>

    @Query("SELECT * FROM segments WHERE trailId=:trailId")
    suspend fun SegmentGetList(trailId: Long): List<SegmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun TrailInsert(trail: TrailEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun waypointInsertList(waypoints: List<WaypointEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun segmentInsertList(segments: List<SegmentEntity>)

    @Transaction
    @Insert
    suspend fun insertTrailInfo(
        waypoints: List<WaypointEntity>,
        segments: List<SegmentEntity>,
    ) {
        waypointInsertList(waypoints)
        segmentInsertList(segments)
    }

    @Query("DELETE FROM trails WHERE id=:id")
    suspend fun TrailDelete(id:Long)

    @Query ("DELETE FROM trails")
    suspend fun TrailDeleteAll()

}

@Database(entities = [TrailEntity::class,WaypointEntity::class,SegmentEntity::class,
    TimerEntity::class],
    version = 496, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val trailDao: TrailDao
}