package pl.put.szlaki.ui.screens.trail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.database.TimerEntity
import pl.put.szlaki.navigators.Screen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TimerScreen(val name:String,val timers: MutableList<TimerEntity>): Screen {
    private lateinit var viewModel: MainViewModel

    private fun deleteTimer(timer: TimerEntity,listUpdated: MutableState<Boolean>) {
        timers.remove(timer)
        viewModel.TimerDelete(timer)
        listUpdated.value=true
    }

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current
        DisposableEffect(Unit) {
            viewModel.timerScreenVisible.value=true
            onDispose { viewModel.timerScreenVisible.value=false }
        }
        viewModel.TopTextChange(name)


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //Refresh list after delete
            val listUpdated = remember { mutableStateOf(false) }
            if (listUpdated.value)
                listUpdated.value = false

            LazyColumn {
                items(timers) {
                    ListItem(it,listUpdated)
                }
                if (timers.isEmpty())
                    item{Text(modifier = Modifier.fillMaxSize(),text = "Brak zapisów", textAlign = TextAlign.Center)}
            }
        }
    }


    @Composable
    private fun ListItem(timer: TimerEntity,listUpdated: MutableState<Boolean>) {
        ElevatedCard(
            modifier =
            Modifier
                .height(150.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .clip(RoundedCornerShape(5, 0, 0, 5)),
            colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            elevation =
            CardDefaults.cardElevation(
                defaultElevation = 20.dp,
            ),
        ) {
            Row(
                modifier =
                Modifier
                    .padding(start = 50.dp),
            ) {
                //Three state bool .-.
                val timerDelete = remember { mutableStateOf<Boolean?>(null) }
                val color =
                    if (timerDelete.value == null) MaterialTheme.colorScheme.primaryContainer else Color.Red
                if (timerDelete.value == false) {
                    LaunchedEffect(timerDelete) {
                        delay(1000L)
                        timerDelete.value = null
                    }
                }

                if (timerDelete.value == true) {
                    deleteTimer(timer, listUpdated)
                    timerDelete.value = null
                }

                Column(
                    modifier =
                    Modifier
                        .weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val date = LocalDateTime.parse(
                        timer.date,
                        DateTimeFormatter.ISO_DATE_TIME
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        fontSize = 22.sp
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        fontSize = 22.sp
                    )
                    Text(text = "Pomiar:", fontSize = 22.sp)
                    Text(text = timer.time, fontSize = 22.sp)

                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color)
                        .fillMaxHeight()
                        .border(BorderStroke(5.dp, Color.White))
                        .clickable(onClick = {
                            if (timerDelete.value == null)
                                timerDelete.value = false
                            else
                                timerDelete.value = true
                        }),
                    contentAlignment = Alignment.Center
                )

                {
                    if (timerDelete.value == false) {
                        Icon(
                            painterResource(R.drawable.delete),
                            contentDescription = "Usuń"
                        )
                    }
                }

            }
        }
    }


}