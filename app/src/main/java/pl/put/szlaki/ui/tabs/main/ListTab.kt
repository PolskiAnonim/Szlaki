package pl.put.szlaki.ui.tabs.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.Navigator
import pl.put.szlaki.navigators.Tab
import pl.put.szlaki.navigators.TabOptions
import pl.put.szlaki.ui.screens.trail.TrailList
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.LocalViewModel


object ListTab: Tab {
    private lateinit var viewModel: MainViewModel
    private var navigator: Navigator = Navigator()

    init {
        navigator.AddScreen(TrailList())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Lista szlaków"
            val icon = painterResource(R.drawable.list)

            return remember {
                TabOptions(
                    title = title,
                    icon = icon
                )
            }
        }
    override val index: UShort
        get() = 0U

    @Composable
    override fun Content() { //dddd
        viewModel = LocalViewModel.current

        navigator.HandleBackPress()

        if (viewModel.FoldableStateGet().collectAsState().value == FoldableDeviceState.CLOSED)
            SingleScreen()
        else
            DoubleScreen()
    }

    @Composable
    fun SingleScreen() {
        navigator.DisplayLast()
    }

    @Composable
    fun DoubleScreen() {
        Row {
            if (navigator.PossibleToEnableDoubleScreen())
                Box(modifier = Modifier.weight(1f)) {
                        navigator.DisplayPenultimate()
                }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                navigator.DisplayLast()
            }
        }
    }
}