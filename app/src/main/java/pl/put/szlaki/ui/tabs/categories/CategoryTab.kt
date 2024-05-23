package pl.put.szlaki.ui.tabs.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.navigators.Navigator
import pl.put.szlaki.navigators.Tab
import pl.put.szlaki.navigators.TabOptions
import pl.put.szlaki.ui.screens.trail.CategoryList

class CategoryTab(
    private val _tabOptions: TabOptions,
    private val tabIndex: UShort,
    length: String
) : Tab {
    private var navigator: Navigator = Navigator()

    init {
        navigator.AddScreen(CategoryList(length))
    }

    private lateinit var viewModel: MainViewModel

    override val options: TabOptions
        @Composable
        get() {
            val title = _tabOptions.title
            val icon: Painter? = null

            return remember {
                TabOptions(
                    title = title,
                    icon = icon
                )
            }
        }

    override val index: UShort
        get() = tabIndex

    @Composable
    override fun Content() {
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