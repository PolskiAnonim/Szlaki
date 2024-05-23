package pl.put.szlaki.ui.screens.main

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.room.Room
import pl.put.szlaki.model.LocalViewModel
import pl.put.szlaki.model.MainViewModel
import pl.put.szlaki.R
import pl.put.szlaki.navigators.LoadingAndMainScreens
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.database.DatabaseHandling
import pl.put.szlaki.database.refreshRepository
import pl.put.szlaki.navigators.Screen

class LoadingScreen: Screen {
    lateinit var viewModel: MainViewModel

    fun provideAppDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Szlaki",
        ).fallbackToDestructiveMigration().build()
    }

    @Composable
    override fun Content() {
        viewModel = LocalViewModel.current
        //Create database
        viewModel.databaseHandling = DatabaseHandling(provideAppDatabase(LocalContext.current))
        //Load library
        refreshRepository(viewModel)

        Visible()
    }

    //Visible Content
    @Composable
    private fun Visible() {
        val context = LocalContext.current
        val imageBitmap = ImageBitmap.imageResource(context.resources, R.mipmap.ic_launcher_foreground)

        val imageWidth = imageBitmap.width
        val imageHeight = imageBitmap.height

        val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
        val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels

        // Define states
        var state = remember { mutableStateOf("start")}

        // Create a transition
        val transition = updateTransition(targetState = state.value, label = "imageTransition")

        // Define animation for each part
        val leftPartOffsetX by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 3000, easing = FastOutSlowInEasing) },
            label = "leftPartOffsetX"
        ) { state ->
            if (state == "start") -imageWidth / 3f else screenWidth / 2f - imageWidth / 2f + 1
        }

        val centerPartOffsetY by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 3000, easing = FastOutSlowInEasing) },
            label = "centerPartOffsetY"
        ) { state ->
            if (state == "start") -imageHeight.toFloat() else screenHeight / 2f - imageHeight / 2f + 1
        }

        val rightPartOffsetX by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 3000, easing = FastOutSlowInEasing) },
            label = "rightPartOffsetX"
        ) { state ->
            if (state == "start") screenWidth.toFloat() else screenWidth / 2f + imageWidth / 6f
        }

        LaunchedEffect(Unit) {
            state.value="stop"
        }
        LaunchedEffect(viewModel.TrailListUpdateGet().collectAsState().value,transition.isRunning) {
            if (viewModel.TrailListUpdateGet().value && !transition.isRunning) {
                LoadingAndMainScreens.ChangeScreen();
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.onPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()

                        // Rysowanie lewej części obrazu
                        canvas.drawImageRect(
                            image = imageBitmap,
                            srcOffset = IntOffset(0, 0),
                            srcSize = IntSize(imageWidth / 3, imageHeight),
                            dstOffset = IntOffset(leftPartOffsetX.toInt(), screenHeight / 2 - imageHeight / 2),
                            dstSize = IntSize(imageWidth / 3, imageHeight),
                            paint = paint
                        )

                        // Rysowanie środkowej części obrazu
                        canvas.drawImageRect(
                            image = imageBitmap,
                            srcOffset = IntOffset(imageWidth / 3, 0),
                            srcSize = IntSize(imageWidth / 3, imageHeight),
                            dstOffset = IntOffset(screenWidth / 2-imageWidth/6, centerPartOffsetY.toInt()),
                            dstSize = IntSize(imageWidth / 3, imageHeight),
                            paint = paint
                        )

                        // Rysowanie prawej części obrazu
                        canvas.drawImageRect(
                            image = imageBitmap,
                            srcOffset = IntOffset(2 * imageWidth / 3, 0),
                            srcSize = IntSize(imageWidth / 3, imageHeight),
                            dstOffset = IntOffset(rightPartOffsetX.toInt(), screenHeight / 2 - imageHeight / 2),
                            dstSize = IntSize(imageWidth / 3, imageHeight),
                            paint = paint
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}