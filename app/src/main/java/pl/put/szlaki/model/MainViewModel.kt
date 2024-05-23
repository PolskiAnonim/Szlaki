package pl.put.szlaki.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.put.szlaki.database.TimerEntity
import pl.put.szlaki.database.DatabaseHandling
import pl.put.szlaki.database.refreshRepository
import pl.put.szlaki.domain.TrailInList
import pl.put.szlaki.domain.parseTrail

var LocalViewModel= compositionLocalOf<MainViewModel> { error("No ViewModel found!") }

enum class FoldableDeviceState{
    HALF_OPENED,
    OPENED,
    CLOSED
}


class MainViewModel: ViewModel() {
    //-------------------------Top text-------------------------------
    private var _topText= MutableStateFlow("")
    fun TopTextChange(value: String) {
        _topText.value = value
    }

    fun TopTextGet():StateFlow<String> {
        return _topText.asStateFlow()
    }

    //----------------------Search-----------------------------
    private val _searchText=MutableStateFlow("")
    @Composable
    fun SearchTextGet():String {
        return _searchText.collectAsState().value
    }
    fun SearchTextChange(text:String) {
        _searchText.value=text
    }

    //---------------------------Foldable device state----------------------------------
    private val _foldableDeviceState = MutableStateFlow(FoldableDeviceState.CLOSED)
    fun FoldableStateUpdate(newState: FoldableDeviceState) {
        _foldableDeviceState.value = newState
    }
    fun FoldableStateGet(): StateFlow<FoldableDeviceState> {
        return _foldableDeviceState.asStateFlow()
    }

    //-----------------------------Speed--------------------------------
    private var _speedButton=MutableStateFlow<Short>(1)
    private var _speed=MutableStateFlow(-1f)

    @Composable
    fun SpeedGet(): Float {
        return _speed.asStateFlow().collectAsState().value
    }
    fun SpeedSelectedButtonGet():StateFlow<Short> {
        return _speedButton.asStateFlow()
    }
    fun SpeedSelectedButtonSet(selectedButton: Short, value: Float=-1F) {
        when(selectedButton) {
            1.toShort() -> _speed.value=-1f
            2.toShort() -> _speed.value=2.2f
            3.toShort() -> _speed.value=7.2f
            4.toShort() -> _speed.value=value
        }
        _speedButton.value=selectedButton
    }

    //--------------------------------Trails------------------------------------
    lateinit var databaseHandling: DatabaseHandling

    //Update list
    private val _trailsUpdated = MutableStateFlow(false)

    fun TrailListUpdateSet() {
        _trailsUpdated.value=true
    }

    fun TrailListUpdateGet():StateFlow<Boolean> {
        return _trailsUpdated.asStateFlow()
    }

    fun TrailListUpdateReset() {
        _trailsUpdated.value=false
    }


    //Trails list
    val trails = MutableStateFlow(mutableListOf<TrailInList>())

    fun TrailDelete(id:Long) {
        trails.value.remove(trails.value.first { it.id == id })
        _trailsUpdated.value=true
        viewModelScope.launch {
            databaseHandling.TrailDelete(id)
        }
    }
    fun TrailDeleteAll() {
        viewModelScope.launch {
            databaseHandling.TrailDeleteAll()
            refreshRepository(this@MainViewModel)
        }
    }

    fun TrailAdd(strings: MutableList<String>) {
        viewModelScope.launch {
            for (string in strings) {
                val trail = parseTrail(string)
                databaseHandling.TrailInsert(trail)
            }
            refreshRepository(this@MainViewModel)
        }
    }

    //------------------------------------Timer----------------------------------------------
    val timerTime = MutableStateFlow(0L)
    val timerIsRunning = MutableStateFlow(false)
    val timerIsStopped=MutableStateFlow(true)
    val timerScreenVisible = MutableStateFlow(false)

    fun TimerDelete(timer: TimerEntity) {
        viewModelScope.launch {
            databaseHandling.TimerDelete(timer)
        }
    }

    fun TimerDeleteAll() {
        viewModelScope.launch {
            databaseHandling.TimerDeleteAll()
        }
    }

    fun TimerSave(time: TimerEntity) {
        viewModelScope.launch {
            databaseHandling.TimerInsert(time)
        }
    }

}