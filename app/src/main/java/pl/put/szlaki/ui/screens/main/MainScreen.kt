package pl.put.szlaki.ui.screens.main

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.Screen
import pl.put.szlaki.navigators.Tab
import pl.put.szlaki.navigators.TabNavigator
import pl.put.szlaki.ui.tabs.main.CategoriesTab
import pl.put.szlaki.ui.tabs.main.ListTab
import pl.put.szlaki.ui.tabs.main.SettingsTab

object MainScreen: Screen {
    lateinit var viewModel: MainViewModel
    private val tabNavigator=TabNavigator(
        tabs = listOf(ListTab, CategoriesTab, SettingsTab),
        initialIndex =  ListTab.index,
        true
    )

    @Composable
    fun CheckForWindowSizeChanges() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val windowInfoTracker = WindowInfoTracker.getOrCreate(context)
        // Use a state to hold the latest WindowLayoutInfo
        DisposableEffect(Unit) {
            val windowLayoutInfoFlow = windowInfoTracker.windowLayoutInfo(context)
            val job = scope.launch {
                windowLayoutInfoFlow.collect { newLayoutInfo ->
                    val foldingFeature =
                        newLayoutInfo.displayFeatures.filterIsInstance<FoldingFeature>()
                            .firstOrNull()
                    if (foldingFeature == null)
                        viewModel.FoldableStateUpdate(FoldableDeviceState.CLOSED)
                    else {
                        if (foldingFeature.state == FoldingFeature.State.FLAT)
                            viewModel.FoldableStateUpdate(FoldableDeviceState.OPENED)
                        else
                            viewModel.FoldableStateUpdate(FoldableDeviceState.HALF_OPENED)
                    }
                }
            }
            onDispose {
                job.cancel()
            }
        }

    }

    @Composable
    fun ColumnScope.TabNavigationDrawerItem(tab: Tab) {
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.colorScheme.onSurfaceVariant,
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.colorScheme.onSurfaceVariant
            ),
            selected = tabNavigator.current.index == tab.index,
            onClick = { tabNavigator.ChangeTab(tab.index) },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { Text(text = tab.options.title) }
        )
    }

    @Composable
    fun ColumnScope.HideNavigationDrawer(drawerState: DrawerState, scope: CoroutineScope) {
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.colorScheme.onSurfaceVariant,
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.colorScheme.onSurfaceVariant
            ),
            icon = { Icon(painterResource(id = R.drawable.back), contentDescription = "close") },
            label = { Text(text = "Schowaj") },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                }
            }
        )
    }

    @Composable
    fun ColumnScope.ShowNavigationDrawer(drawerState: DrawerState, scope: CoroutineScope) {
        NavigationRailItem(
            selected = false, onClick = {
                scope.launch {
                    drawerState.open()
                }
            },
            icon = { Icon(painterResource(id = R.drawable.start), contentDescription = "open") })
    }

    @Composable
    fun ColumnScope.TabNavigationRailItem(tab: Tab) {
        NavigationRailItem(
            selected = tabNavigator.current.index == tab.index,
            onClick = { tabNavigator.ChangeTab(tab.index) },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationHorizontal() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.primaryContainer) {
                HideNavigationDrawer(drawerState, scope)
                TabNavigationDrawerItem(ListTab)
                TabNavigationDrawerItem(CategoriesTab)
                TabNavigationDrawerItem(SettingsTab)
            }
        }, drawerState = drawerState) {
            Row {
                NavigationRail(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    ShowNavigationDrawer(drawerState = drawerState, scope = scope)
                    TabNavigationRailItem(ListTab)
                    TabNavigationRailItem(CategoriesTab)
                    TabNavigationRailItem(SettingsTab)
                }
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                val topText= viewModel.TopTextGet().collectAsState().value
                                Text(
                                    text = topText,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        )
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .padding(it)
                        ) {
                            tabNavigator.CurrentTab()
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationPortraitOrOpened() {
        Scaffold(
            topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            val topText= viewModel.TopTextGet().collectAsState().value
                            Text(
                                text = topText,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
            },
            content = {
                Box(
                    modifier = Modifier
                        .padding(it)
                ) {
                    tabNavigator.CurrentTab()
                }
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    TabNavigationItem(ListTab)
                    TabNavigationItem(CategoriesTab)
                    TabNavigationItem(SettingsTab)
                }
            }
        )
    }

    @Composable
    fun RowScope.TabNavigationItem(tab: Tab) {
        NavigationBarItem(
            selected = tabNavigator.current.index == tab.index,
            onClick = { tabNavigator.ChangeTab(tab.index) },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
        )
    }


    @Composable
    override fun Content() {
        CheckForWindowSizeChanges()
        viewModel = LocalViewModel.current

        Surface(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
            ),
            color = MaterialTheme.colorScheme.background
        ) {
            val orientation = LocalConfiguration.current.orientation
            if (orientation == ORIENTATION_PORTRAIT ||
                viewModel.FoldableStateGet().collectAsState().value != FoldableDeviceState.CLOSED
            )
                NavigationPortraitOrOpened()
            else
                NavigationHorizontal()
        }
    }
}
