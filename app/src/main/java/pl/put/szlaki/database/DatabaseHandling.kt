package pl.put.szlaki.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.domain.*

fun refreshRepository(viewModel: MainViewModel) {
    viewModel.viewModelScope.launch {
        viewModel.trails.value.clear()
        val trails = viewModel.databaseHandling.TrailGetList()
        viewModel.trails.value.addAll(trails)
        viewModel.TrailListUpdateSet()
    }
}

class DatabaseHandling(
    private val appDatabase: AppDatabase,
) {
    //Timers
    @WorkerThread
    suspend fun TimerGetList(id:Long):List<TimerEntity> {
        return appDatabase.trailDao.TimerGetList(id)
    }

    @WorkerThread
    suspend fun TimerInsert(timerEntity: TimerEntity) {
        return appDatabase.trailDao.TimerInsert(timerEntity)
    }

    @WorkerThread
    suspend fun TimerDelete(timerEntity: TimerEntity) {
        return appDatabase.trailDao.TimerDelete(timerEntity)
    }

    @WorkerThread
    suspend fun TimerDeleteAll() {
        return appDatabase.trailDao.TimerDeleteAll()
    }
    //Trails

    @WorkerThread
    suspend fun TrailGetList():List<TrailInList> {
        return appDatabase.trailDao.TrailGetList().AsDomainModel()
    }

    @WorkerThread
    suspend fun TrailGet(trinl: TrailInList): Trail {
        val trail = Trail(trinl.id,trinl.name,trinl.length,trinl.bounds,trinl.timeStart,trinl.timeEnd,
            mutableListOf(), mutableListOf())
        trail.waypoints.addAll(appDatabase.trailDao.WaypointGetList(trinl.id).AsDomainModel())
        trail.segments.addAll(appDatabase.trailDao.SegmentGetList(trinl.id).AsDomainModel())

        return trail
    }

    @WorkerThread
    suspend fun TrailInsert(
        trail: Trail
    ) {
        val id=appDatabase.trailDao.TrailInsert(trail.AsDatabaseModel())
        appDatabase.trailDao.insertTrailInfo(
            trail.waypoints.AsDatabaseModel(id),
            trail.segments.AsDatabaseModel(id)
        )
    }

    @WorkerThread
    suspend fun TrailDelete(
        id: Long
    ) {
        appDatabase.trailDao.TrailDelete(id)
    }

    @WorkerThread
    suspend fun TrailDeleteAll() {
        appDatabase.trailDao.TrailDeleteAll()
    }

}

