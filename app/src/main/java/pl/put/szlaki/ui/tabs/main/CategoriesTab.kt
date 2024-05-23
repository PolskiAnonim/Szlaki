package pl.put.szlaki.ui.tabs.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.Tab
import pl.put.szlaki.navigators.TabNavigator
import pl.put.szlaki.navigators.TabOptions

import pl.put.szlaki.ui.tabs.categories.CategoryTab
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

object CategoriesTab : Tab {

    lateinit var viewModel: MainViewModel

    private var categoryTabs:List<Tab> = listOf<Tab>(
        CategoryTab(TabOptions("Krótkie"), 0U,"k"),
        CategoryTab(TabOptions("Średnie"), 1U,"m"),
        CategoryTab(TabOptions("Długie"), 2U,"l")
    )
    private var tabNavigator: TabNavigator = TabNavigator(tabs = categoryTabs
            , initialIndex = 0U,false)

    override val options: TabOptions
        @Composable
        get() {
            val title = "Szlaki podzielone ze względu na długość"
            val icon = painterResource(R.drawable.categories)

            return remember {
                TabOptions(
                    title = title,
                    icon = icon
                )
            }
        }

    override val index: UShort
        get() = 1U

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current

        Box {
            val orientation = LocalConfiguration.current.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT ||
                viewModel.FoldableStateGet().collectAsState().value != FoldableDeviceState.CLOSED
            )
                NavigationPortraitOrOpened()
            else
                NavigationHorizontal()
        }
    }

    @Composable
    private fun NavigationHorizontal() {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                tabNavigator.CurrentTab()
            }
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxHeight()
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                categoryTabs.forEach { tab ->
                    Box(
                        modifier = Modifier
                            .weight(0.33f)
                            .fillMaxWidth()
                            .clickable { tabNavigator.ChangeTab(tab.index) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(32.dp)
                                    .background(if (tabNavigator.currentIndex == tab.index) MaterialTheme.colorScheme.onPrimary else Color.Transparent)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = tab.options.title,
                                    color = if (tabNavigator.currentIndex == tab.index) Color.White else Color.Gray,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationPortraitOrOpened() {
        Scaffold(
            topBar = {
                TopBar()
            }
        ) {
            Box(Modifier.padding(it)) {
                tabNavigator.CurrentTab()
            }
        }
    }

    @Composable
    private fun TopBar() {
        TabRow(
            selectedTabIndex = tabNavigator.currentIndex.toInt(),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            contentColor = Color.White,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[tabNavigator.currentIndex.toInt()]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) {
            categoryTabs.forEach { tab ->
                Tab(
                    selected = tabNavigator.currentIndex == tab.index,
                    onClick = { tabNavigator.ChangeTab(tab.index) },
                    text = {
                        Text(
                            text = tab.options.title,
                            color = if (tabNavigator.currentIndex == tab.index) Color.White else Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                )
            }
        }
    }
}