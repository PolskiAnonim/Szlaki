package pl.put.szlaki.ui.screens.trail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.Screen
import pl.put.szlaki.ui.components.TrailListItem

class TrailList: Screen {

    private lateinit var viewModel: MainViewModel

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current
        viewModel.TopTextChange("Lista szlaków")

        if (viewModel.TrailListUpdateGet().collectAsState().value)
            viewModel.TrailListUpdateReset()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item { TopListBar() }
            items(viewModel.trails.value) { trail ->
                if (trail.name.contains(viewModel.SearchTextGet(),true))
                    TrailListItem(trail).Content()
            }
        }
    }

    @Composable
    fun TopListBar() {
        val addDialogOpened = remember { mutableStateOf(false) }
        if (addDialogOpened.value) {
            AddDialog(addDialogOpened, viewModel)
        }

        val searchText=viewModel.SearchTextGet()

        Row(
            Modifier
                .fillMaxWidth()
                .height(65.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
                onValueChange = {viewModel.SearchTextChange(it)},
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                placeholder = { Text(text = "Wyszukaj szlaki") },
            )
            Button(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(),
                onClick = {
                    addDialogOpened.value = true
                },
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =  MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painterResource(id = R.drawable.add),
                    "Dodaj szlak",
                )
            }
        }
    }

    //------------------------------Adding trails-----------------------------------------------

    @Composable
    private fun AddDialog(
        openAlertDialog: MutableState<Boolean>,
        viewModel: MainViewModel
    ) {

        val context = LocalContext.current

        val selectedFile = remember { mutableStateListOf<Uri?>(null) }

        val fileLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { files ->
                selectedFile.apply {
                    clear()
                    addAll(files)
                }
                val strings = mutableListOf<String>()
                for (file in files) {
                    context.contentResolver.openInputStream(file)?.bufferedReader()?.readText()
                        ?.let { strings.add(it) }
                }
                viewModel.TrailAdd(strings)
                openAlertDialog.value = false
            }

        AlertDialog(
            title = { Text(text = "Dodaj nowy szlak") },
            text = { Text(text = "Dodaj nowy szlak z pliku .gpx") },
            onDismissRequest = { openAlertDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        fileLauncher.launch("*/*")
                    }
                ) {
                    Text("Dodaj szlak")
                }

            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                    }
                ) {
                    Text("Anuluj")
                }
            })
    }
}
