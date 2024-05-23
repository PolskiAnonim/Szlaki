package pl.put.szlaki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.navigators.LoadingAndMainScreens
import pl.put.szlaki.ui.theme.SzlakiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<MainViewModel>()

        setContent {
            SzlakiTheme {
                CompositionLocalProvider(LocalViewModel provides viewModel) {
                    LoadingAndMainScreens.Display()
                }
            }
        }
    }
}

