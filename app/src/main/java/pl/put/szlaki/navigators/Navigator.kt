package pl.put.szlaki.navigators

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import pl.put.szlaki.ui.screens.main.LoadingScreen
import pl.put.szlaki.ui.screens.main.MainScreen
//It's only for displaying Loading Screen before list loads
object LoadingAndMainScreens {
    var screenToDisplay= mutableStateOf<Screen>(LoadingScreen())
    @Composable
    fun Display() {
        screenToDisplay.value.Content()
    }

    fun ChangeScreen() {
        screenToDisplay.value= MainScreen
    }
}


class Navigator {
    private val stack= mutableStateListOf<Screen>()
    private val callback=object: OnBackPressedCallback(false){
        override fun handleOnBackPressed() {
            removeScreen()
        }
    }
    //Back press
    @Composable
    fun HandleBackPress(){
        val backDispatcherOwner=LocalOnBackPressedDispatcherOwner.current!!
        //Add if it is in Composition and delete if it is not
        //or if backDispatcher changed (orientation change)
        DisposableEffect(backDispatcherOwner.onBackPressedDispatcher) {
            initializeBackCallback(backDispatcherOwner.onBackPressedDispatcher, backDispatcherOwner)
            onDispose { removeBackCallback() }
        }
    }
    //Add callback
    private fun initializeBackCallback(dispatcher: OnBackPressedDispatcher,owner: LifecycleOwner) {
        dispatcher.addCallback(owner,callback)
        callback.isEnabled=stack.size>1
    }
    //Remove callback
    private fun removeBackCallback() {
        callback.remove()
    }

    @Composable
    fun DisplayLast() {
        // Provide the current navigator instance if needed
        CompositionLocalProvider(LocalNavigator provides this) {
            stack.last().Content()
        }
    }

    @Composable
    fun DisplayPenultimate() {
        CompositionLocalProvider(LocalNavigator provides this) {
        if (stack.size>1)
            stack[stack.size-2].Content()
        }
    }

    fun AddScreen(screen: Screen) {
        tryEnableCallback()
        if (stack.size>1 && screen::class == stack.last()::class) {
            stack.removeLast()
            stack.add(screen)
        }
        else
            stack.add(screen)
    }

    fun removeScreen() {
        stack.removeLast()
        tryDisableCallback()
    }

    private fun tryDisableCallback() {
        if (stack.size==1)
            callback.isEnabled = false
    }

    private fun tryEnableCallback() {
        if (stack.size==1)
            callback.isEnabled = true
    }

    fun PossibleToEnableDoubleScreen():Boolean {
        if (stack.size>1)
            return true
        else
            return false
    }
}



