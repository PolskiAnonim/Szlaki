package pl.put.szlaki.ui.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import pl.put.szlaki.model.FoldableDeviceState
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.navigators.Screen
import pl.put.szlaki.ui.tabs.main.SettingsTab

object SettingsScreen: Screen {
    private lateinit var viewModel: MainViewModel



    @Composable
    private fun ConfirmDialog(openDialog: MutableState<Boolean?>) {
        val title=if (openDialog.value == true) "Usunięcie szlaków" else "Usunięcie czasomierzy"
        val text=if (openDialog.value == true) "Czy na pewno chcesz wszystkie szlaki?" else "Czy na pewno chcesz wszystkie czasomierze?"
        val onClick={if (openDialog.value == true) viewModel.TrailDeleteAll() else viewModel.TimerDeleteAll()}
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = text) },
            onDismissRequest = { openDialog.value = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClick()
                        openDialog.value = null
                    }
                ) {
                    Text("Usuń")
                }

            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = null
                    }
                ) {
                    Text("Anuluj")
                }
            })
    }

    private val speed = MutableStateFlow("")

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SpeedSettings(modifier: Modifier) {
        val boxModifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
        Box(modifier = boxModifier) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Ustaw szybkość chodu")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .height(50.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(enabled = SettingsTab.viewModel.SpeedSelectedButtonGet()
                        .collectAsState().value != 1.toShort(),
                        onClick = { SettingsTab.viewModel.SpeedSelectedButtonSet(1) }) {
                        Text(text = "Domyślna")
                    }
                    Button(enabled = SettingsTab.viewModel.SpeedSelectedButtonGet()
                        .collectAsState().value != 2.toShort(),
                        onClick = { SettingsTab.viewModel.SpeedSelectedButtonSet(2) }) {
                        Text(text = "Wolno")
                    }
                    Button(enabled = SettingsTab.viewModel.SpeedSelectedButtonGet()
                        .collectAsState().value != 3.toShort(),
                        onClick = { SettingsTab.viewModel.SpeedSelectedButtonSet(3) }) {
                        Text(text = "Szybko")
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .height(50.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val vmSpeed = viewModel.SpeedGet()
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = (vmSpeed != speed.collectAsState().value.toFloatOrNull() &&
                                viewModel.SpeedSelectedButtonGet().collectAsState().value > 3
                                ),
                        onClick = { speed.value = vmSpeed.toString() }) {
                        Text(text = (if (vmSpeed == -1f) "-" else "$vmSpeed"), maxLines = 1)
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = ((SettingsTab.viewModel.SpeedSelectedButtonGet()
                            .collectAsState().value != 4.toShort()
                                || vmSpeed != speed.collectAsState().value.toFloatOrNull())
                                && speed.collectAsState().value.toFloatOrNull() != null),
                        onClick = {
                            SettingsTab.viewModel.SpeedSelectedButtonSet(
                                4,
                                speed.value.toFloat()
                            )
                        }) {
                        Text(text = "Dokładna")
                    }
                    //For TextField
                    val interactionSource = remember { MutableInteractionSource() }
                    BasicTextField(
                        value = speed.collectAsState().value,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                speed.value = newValue
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 5.dp)
                            .height(40.dp),
                        interactionSource = interactionSource,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                        decorationBox = { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = speed.collectAsState().value,
                                visualTransformation = VisualTransformation.None,
                                innerTextField = innerTextField,
                                singleLine = true,
                                enabled = true,
                                interactionSource = interactionSource,
                                placeholder = {
                                    Text(
                                        text = "Wpisz",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                },
                                suffix = {
                                    Text(
                                        text = "km/h",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                },
                                colors = TextFieldDefaults.colors( //I hate people who created this
                                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                                    errorTextColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    errorContainerColor = MaterialTheme.colorScheme.primary,
                                    selectionColors = TextSelectionColors(
                                        MaterialTheme.colorScheme.onPrimary,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    disabledIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    errorIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedSuffixColor = MaterialTheme.colorScheme.onPrimary,
                                    unfocusedSuffixColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledSuffixColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(60),
                                contentPadding = PaddingValues(
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom = 5.dp
                                ),
                            )
                        }
                    )

//                    TextField(
//                        value = SettingsTab.speed.collectAsState().value,
//                        suffix = { Text(text = "km/h",fontSize = 14.sp) },
//                        onValueChange = { newValue ->
//                            if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
//                                SettingsTab.speed.value=newValue
//                            }},
//                        maxLines = 1,
//                        textStyle = MaterialTheme.typography.bodyMedium,
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        colors = TextFieldDefaults.colors( //I hate people who created this
//                            focusedTextColor=Color.Black,
//                            unfocusedTextColor=Color.Black,
//                            focusedContainerColor=MaterialTheme.colorScheme.primary,
//                            unfocusedContainerColor=MaterialTheme.colorScheme.primary,
//                            disabledContainerColor=MaterialTheme.colorScheme.primary,
//                            errorContainerColor=MaterialTheme.colorScheme.primary,
//                            cursorColor=MaterialTheme.colorScheme.primaryContainer,
//                            focusedIndicatorColor=MaterialTheme.colorScheme.primaryContainer,
//                            unfocusedIndicatorColor=MaterialTheme.colorScheme.primaryContainer,
//                            disabledIndicatorColor=MaterialTheme.colorScheme.primaryContainer,
//                            errorIndicatorColor=MaterialTheme.colorScheme.primaryContainer,
//                            focusedSuffixColor=MaterialTheme.colorScheme.primary,
//                            unfocusedSuffixColor=MaterialTheme.colorScheme.primary,
//                            disabledSuffixColor=MaterialTheme.colorScheme.primary
//                        ),
//                        shape = RoundedCornerShape(60),
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp, vertical = 8.dp)
//                            .height(35.dp)
//                    )
                }
            }
        }
    }

    @Composable
    private fun AnotherButtons(modifier: Modifier) {
        val openDialog= remember { mutableStateOf<Boolean?>(null) }
        if (openDialog.value!=null) {
            ConfirmDialog(openDialog)
        }

        Column(
            modifier.width(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { openDialog.value=false }) {
                Text(text = "Usuń czasomierze", textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { openDialog.value=true }) {
                Text(text = "Usuń wszystkie szlaki", textAlign = TextAlign.Center)
            }
        }
    }

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current

        val orientation = LocalConfiguration.current.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT &&
            viewModel.FoldableStateGet().collectAsState().value == FoldableDeviceState.CLOSED
        )
            SingleColumn()
        else
            SingleRow()
    }

    @Composable
    fun SingleColumn() {
        SettingsTab.viewModel.TopTextChange("Ustawienia")

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            SpeedSettings(Modifier.padding(horizontal = 5.dp, vertical = 10.dp))
            AnotherButtons(Modifier)
        }
    }

    @Composable
    fun SingleRow() {
        SettingsTab.viewModel.TopTextChange("Ustawienia")
        Row(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpeedSettings(Modifier.weight(1f))
            AnotherButtons(Modifier)
        }
    }
}