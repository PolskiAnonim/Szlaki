package pl.put.szlaki.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.R

@Composable
fun LoadingDetails() {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary),
        contentAlignment = Alignment.Center,

        ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(300.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}