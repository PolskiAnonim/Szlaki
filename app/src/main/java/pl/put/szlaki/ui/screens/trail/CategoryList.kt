package pl.put.szlaki.ui.screens.trail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.domain.TrailInList
import pl.put.szlaki.navigators.Screen
import pl.put.szlaki.ui.components.TrailListItem
import pl.put.szlaki.ui.tabs.main.CategoriesTab

class CategoryList(val length: String):Screen {

    val predicate: (TrailInList) -> Boolean = { trail: TrailInList ->
        if (length=="k" && trail.length<10)
            true
        else if (length=="m" && trail.length>=10 && trail.length<25)
            true
        else if (length=="l" && trail.length>=25)
            true
        else
            false
    }

    private lateinit var viewModel: MainViewModel

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current

        CategoriesTab.viewModel.TopTextChange("Szlaki podzielone ze względu na długość")

        val filteredTrails=viewModel.trails.collectAsState().value.filter(predicate)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item { TopListBar() }
            items(filteredTrails) { trail ->
                if (trail.name.contains(viewModel.SearchTextGet(), true))
                    TrailListItem(trail).Content()
            }
        }
    }

    @Composable
    fun TopListBar() {
        val searchText = viewModel.SearchTextGet()

        Row(
            Modifier
                .fillMaxWidth()
                .height(65.dp), horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .weight(0.6f),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors =
                TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                value = searchText,
                onValueChange = { viewModel.SearchTextChange(it) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                placeholder = { Text(text = "Wyszukaj szlaki") },
            )
        }
    }
}