package pl.put.szlaki.navigators

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter

val LocalNavigator = compositionLocalOf<Navigator> { error("No Navigator found!") }

//Screen for Navigator
interface Screen {
    @Composable
    fun Content()
}

//Tabs for TabNavigator
data class TabOptions(
    val title: String,
    val icon: Painter? = null
)

interface Tab : Screen {
    val options: TabOptions     //Title and icon
        @Composable get
    val index:UShort    //Index for changing tab
}