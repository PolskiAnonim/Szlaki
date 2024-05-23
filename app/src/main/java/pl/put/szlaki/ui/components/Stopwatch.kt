package pl.put.szlaki.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.*
import pl.put.szlaki.R
import pl.put.szlaki.database.TimerEntity
import pl.put.szlaki.navigators.LocalNavigator
import pl.put.szlaki.navigators.Navigator
import pl.put.szlaki.ui.screens.trail.TimerScreen
import java.time.LocalDateTime


data class StopwatchFragment(val id:Long,val name:String) {
    private lateinit var timerList:MutableList<TimerEntity>
    private lateinit var localNavigator: Navigator
    private lateinit var viewModel: MainViewModel
    fun ChangeScreen() {
        localNavigator.AddScreen(TimerScreen(name,timerList))
    }

    private fun timerRun() {
        CoroutineScope(Dispatchers.Main).launch {
            while (!viewModel.timerIsStopped.value) {
                delay(1000L)
                if (viewModel.timerIsRunning.value)
                    viewModel.timerTime.value++
            }
        }
    }

    //Database
    private fun timerGet() {
        viewModel.viewModelScope.launch {
            timerList=viewModel.databaseHandling.TimerGetList(id).toMutableList()
        }
    }

    private fun timerToStringTime(constTime: Long): String {
        var time=constTime
        val hours=time/3600
        time-=(hours*3600)
        val minutes=time/60
        time-=(minutes*60)
        return "$hours:$minutes:$time"
    }

    private fun timerSave() {
        val time=TimerEntity(id,LocalDateTime.now().toString(),
            timerToStringTime(viewModel.timerTime.value))
        timerList.add(time)
        //Refresh screen with results...
        if (viewModel.FoldableStateGet().value!= FoldableDeviceState.CLOSED && viewModel.timerScreenVisible.value)
            ChangeScreen()

        viewModel.TimerSave(time)
    }


    @Composable
    private fun VisibleStopwatch(timerShowTimer: MutableState<Boolean>) {
        val timerOpenListScreen= remember { mutableStateOf(false)}

        if (timerOpenListScreen.value) {
            timerOpenListScreen.value=false
            ChangeScreen()
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (modifier= Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center){

                Text(text = timerToStringTime(viewModel.timerTime.collectAsState().value), fontSize = 32.sp)
            }
            Row {
                FloatingActionButton(onClick = {
                    if (!viewModel.timerIsRunning.value && viewModel.timerIsStopped.value) {
                        viewModel.timerIsRunning.value=true
                        viewModel.timerIsStopped.value=false
                        viewModel.timerTime.value=0L
                        timerRun()
                    }
                    else if (!viewModel.timerIsRunning.value) {
                        viewModel.timerIsRunning.value=true
                    }
                    else
                        viewModel.timerIsRunning.value=false}
                ) {
                    if (viewModel.timerIsRunning.collectAsState().value)
                        Icon(
                            painter = painterResource(R.drawable.pause),
                            contentDescription = "Pauza"
                        )
                    else
                        Icon(
                            painter = painterResource(R.drawable.start),
                            contentDescription = "Start"
                        )
                }
                FloatingActionButton(onClick = {
                    viewModel.timerIsRunning.value=false
                    viewModel.timerIsStopped.value=true
                    timerSave()
                    viewModel.timerTime.value=0L
                }) {
                    Icon(painterResource(R.drawable.stop),contentDescription = "Stop")
                }

                FloatingActionButton(onClick = { timerOpenListScreen.value = true }) {
                    Text(text = "Wyniki")
                }
                FloatingActionButton(onClick = { timerShowTimer.value = false }) {
                    Text(text = "Schowaj")
                }
            }
        }

    }

    @Composable
    fun Content() {
        viewModel= LocalViewModel.current
        localNavigator= LocalNavigator.current
        timerGet()

        val timerShowTimer = rememberSaveable { mutableStateOf(false) }
        if (timerShowTimer.value)
            VisibleStopwatch(timerShowTimer)
        else
            HiddenStopwatch(timerShowTimer)

    }

    @Composable
    private fun HiddenStopwatch(open: MutableState<Boolean>) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(onClick = { open.value = true }) {
                Text(text = "Pokaż stoper")
            }
        }
    }
}