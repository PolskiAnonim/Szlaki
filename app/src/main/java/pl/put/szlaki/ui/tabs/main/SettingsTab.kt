package pl.put.szlaki.ui.tabs.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.Tab
import pl.put.szlaki.navigators.TabOptions
import pl.put.szlaki.ui.screens.settings.SettingsScreen

//USTAWIENIA - SZYBKOŚĆ CHODU

object SettingsTab : Tab {

    lateinit var viewModel: MainViewModel

    override val options: TabOptions
        @Composable
        get() {
            val title = "Ustawienia"
            val icon = painterResource(R.drawable.settings)

            return remember {
                TabOptions(
                    title = title,
                    icon = icon
                )
            }
        }

    override val index: UShort
        get() = 2U

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current
        SettingsScreen.Content()
    }


}
