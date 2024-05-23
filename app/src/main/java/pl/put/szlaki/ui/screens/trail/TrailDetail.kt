package pl.put.szlaki.ui.screens.trail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.put.szlaki.model.*
import pl.put.szlaki.domain.Trail
import pl.put.szlaki.domain.TrailSegment
import pl.put.szlaki.domain.TrailWaypoint
import pl.put.szlaki.ui.components.StopwatchFragment
import pl.put.szlaki.navigators.Screen
import pl.put.szlaki.ui.components.LoadingDetails
import java.time.Duration

data class TrailDetail(val trail:MutableState<Trail?>): Screen {
    private lateinit var viewModel: MainViewModel

    @Composable
    override fun Content() {
       viewModel = LocalViewModel.current

        if (trail.value == null)
            LoadingDetails()
        else {
            viewModel.TopTextChange(trail.value!!.name)
            Scaffold(
                bottomBar = { StopwatchFragment(trail.value!!.id, trail.value!!.name).Content() }
            ) { innerPadding ->
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    item { TrailInfoComp(trail.value!!) }
                    item { WaypointsComp(trail.value!!) }
                    item { SegmentsComp(trail.value!!) }
                }
            }
        }
    }

    @Composable
    fun TrailInfoComp(trail: Trail) {
        val time:Duration = if (viewModel.SpeedGet()==-1f) {
            try {
                Duration.between(
                    trail.timeStart, trail.timeEnd
                )
            } catch (_: Exception) {
                Duration.ZERO
            }
        } else {
            Duration.ofSeconds(((trail.length/viewModel.SpeedGet())*3600).toLong())
        }

        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 10.dp)
        )
        {
            Column {
                Text(
                    "Informacje podstawowe:", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp
                )
                Text(text = String.format("Długość: %.3f km",trail.length),
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = "Szacowany czas: ${time.toHours() } h ${ time.toMinutesPart() } min",
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }


    @Composable
    fun WaypointsComp(trail: Trail) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 30.dp)
            )
            {
                Text(
                    "Interesujące punkty", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            for (point in trail.waypoints)
                OneWaypoint(point)
        }

    }

    @Composable
    fun OneWaypoint(point: TrailWaypoint) {
        val boxModifier = Modifier
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Box(
                modifier = boxModifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = "Nazwa:\n${point.name}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                Box(
                    modifier = boxModifier.weight(1f)
                )
                {
                    Text(
                        text = String.format("Szer. geo.\n%.3f°",point.point.latitude),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(
                    modifier = boxModifier.weight(1f)
                )
                {
                    Text(
                        text = String.format("Dł. geo.\n%.3f°",point.point.longitude),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Box(
                modifier = boxModifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = String.format("Wysokość npm:\n%.2f  m",point.point.latitude),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }

    @Composable
    fun SegmentsComp(trail: Trail) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 30.dp)
            )
            {
                Text(
                    "Etapy", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            for (segment in trail.segments)
                OneSegment(segment)
        }
    }

    @Composable
    fun OneSegment(segment: TrailSegment) {
        val boxModifier = Modifier
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Box(
                modifier = boxModifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = String.format("Długość etapu:\n%.2f km",segment.length),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                Box(
                    modifier = boxModifier
                        .weight(1f)
                )
                {
                    Text(
                        text = String.format("W górę:\n%.2f km",segment.upElevation),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = boxModifier
                        .weight(1f)
                )
                {
                    Text(
                        text = String.format("W dół:\n%.2f km",segment.downElevation),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Box(
                modifier = boxModifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = String.format("Średnia wysokość:\n%.2f m",segment.meanElevation),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = boxModifier
                    .fillMaxWidth()
            )
            {
                val time:Duration = if (viewModel.SpeedGet()==-1f) {
                    try {
                        Duration.between(
                            segment.timeStart, segment.timeEnd
                        )
                    } catch (_: Exception) {
                        Duration.ZERO
                    }
                } else {
                    Duration.ofSeconds(((segment.length/viewModel.SpeedGet())*3600).toLong())
                }
                Text(
                    text = String.format("Czas przejścia:\n${time.toHours()} h ${time.toMinutesPart()} m ${time.toSecondsPart()} s "),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }

}
