package pl.put.szlaki.navigators

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import pl.put.szlaki.model.LocalViewModel

class TabNavigator(
    private val tabs: List<Tab>,
    initialIndex: UShort,
    private val swipeEnabled: Boolean =false
) {
    private var directionOfTabAnimation=0
    private val currentIndexState: MutableState<UShort> = mutableStateOf(initialIndex)

    var currentIndex: UShort
        get() = currentIndexState.value
        set(value) {
            currentIndexState.value = value
        }

    val current: Tab
        @Composable
        get() = tabs.first { it.index == currentIndex }

    @Composable
    fun CurrentTab() {
        var offset =0f
        val viewModel= LocalViewModel.current

        val swipeModifier= Modifier
            .fillMaxSize()
            .pointerInput(
                currentIndex,
                viewModel
                    .FoldableStateGet()
                    .collectAsState()
                    .value
            ) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offset > 100) {
                            if (currentIndex > 0U) {
                                directionOfTabAnimation = -1
                                currentIndex--
                            }
                        } else if (offset < -100) {
                            if (currentIndex < (tabs.size - 1).toUShort()) {
                                directionOfTabAnimation = 1
                                currentIndex++
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                }
            }

        Box(
            modifier = if (swipeEnabled) swipeModifier else Modifier.fillMaxSize()
        ) {
            tabs.forEach { tab ->
                AnimatedVisibility(
                    visible = tab.index == currentIndex,
                    enter = slideInHorizontally(animationSpec = tween(300)) { fullWidth ->
                        if (directionOfTabAnimation == 1) fullWidth else -fullWidth
                    } + fadeIn(),
                    exit = slideOutHorizontally(animationSpec = tween(300)) { fullWidth ->
                        if (directionOfTabAnimation == 1) -fullWidth else fullWidth
                    } + fadeOut()
                ) {
                    tab.Content()
                }
            }
        }
    }

    fun ChangeTab(newIndex: UShort) {
        directionOfTabAnimation = if (currentIndex>newIndex)
            -1 else 1
        currentIndex = newIndex
    }
}