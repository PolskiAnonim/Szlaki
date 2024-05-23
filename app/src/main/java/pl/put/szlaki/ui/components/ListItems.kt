package pl.put.szlaki.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.domain.Trail
import pl.put.szlaki.domain.TrailInList
import pl.put.szlaki.navigators.LocalNavigator
import pl.put.szlaki.ui.screens.trail.TrailDetail
import java.time.Duration

data class TrailListItem(val trinl: TrailInList) {
    var trail = mutableStateOf<Trail?>(null)

    private lateinit var viewModel: MainViewModel

    private fun trailGet() {
        viewModel.viewModelScope.launch {
            trail.value = viewModel.databaseHandling.TrailGet(trinl)
        }
    }

    @Composable
    private fun ChangeScreen() {
        val navigator = LocalNavigator.current
        navigator.AddScreen(TrailDetail(trail))
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Content() {
        viewModel = LocalViewModel.current
        trailGet()
        val time: Duration = if (viewModel.SpeedGet() == -1f) {
            try {
                Duration.between(
                    trinl.timeStart, trinl.timeEnd
                )
            } catch (_: Exception) {
                Duration.ZERO
            }
        } else {
            Duration.ofSeconds(((trinl.length / viewModel.SpeedGet()) * 3600).toLong())
        }

        val open = remember { mutableStateOf(false) }
        val deleteDialog = remember { mutableStateOf(false) }

        //Dialog
        if (deleteDialog.value) {
            DeleteDialog(deleteDialog, trinl.id, viewModel)
        }
        //Details
        if (open.value) {
            open.value = false
            ChangeScreen()
        }

        ElevatedCard(
            modifier =
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .clip(RoundedCornerShape(10, 0, 0, 10))
                .combinedClickable(
                    onClick = { open.value = true },
                    onLongClick = { deleteDialog.value = true }
                ),
            colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            elevation =
            CardDefaults.cardElevation(
                defaultElevation = 20.dp,
            )
        ) {
            CardContent(time)
        }
    }

    @Composable
    fun CardContent(time: Duration) {
        val visibleFullName = remember { mutableStateOf(false) }
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = trinl.name,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Clip,
                    maxLines = (if (visibleFullName.value) 4; else 1)
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Row {
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.street),
                        contentDescription = "points",
                    )
                    Text(
                        modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                        text = String.format("%.3f km", trinl.length)
                    )
                }
                Box(modifier = Modifier
                    .clickable { visibleFullName.value = !visibleFullName.value }) {
                    if (!visibleFullName.value)
                        Icon(
                            painter = painterResource(R.drawable.down),
                            contentDescription = "Roll",
                            tint = colorResource(id = R.color.green)
                        )
                    else {
                        Icon(
                            painter = painterResource(R.drawable.up),
                            contentDescription = "Roll",
                            tint = colorResource(id = R.color.green)
                        )
                    }
                }
                Row {
                    Text(
                        modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                        text = if (time.toSeconds() != 0L) "${time.toHours()} h ${time.toMinutesPart()} min"; else "--"
                    )
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        painter = painterResource(R.drawable.time),
                        contentDescription = "points",
                    )
                }
            }
        }
    }
}

//delete trail
@Composable
private fun DeleteDialog(delete: MutableState<Boolean>, id: Long,viewModel: MainViewModel) {
    AlertDialog(
        title = { Text(text = "Usunięcie szlaku") },
        text = { Text(text = "Czy na pewno chcesz usunąć ten szlak?") },
        onDismissRequest = { delete.value = false },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.TrailDelete(id)
                    delete.value = false
                }
            ) {
                Text("Usuń szlak")
            }

        },
        dismissButton = {
            TextButton(
                onClick = {
                    delete.value = false
                }
            ) {
                Text("Anuluj")
            }
        })
}