package es.uji.smallaris.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.ui.components.LoadingCircle

@Composable
fun LoadingScreen(loadingProcess: suspend ()->Unit, onTimeout: () -> Unit){
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        //Procesos de carga en los viewModel
        loadingProcess()
        currentOnTimeout()
    }
    LoadingScreenContent()
}
@Composable
@Preview
fun PreviewLoadingScreen(){
    LoadingScreenContent()
}

@Composable
private fun LoadingScreenContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(170.dp),
                painter = painterResource(R.drawable.smallaris_icon),
                contentDescription = stringResource(R.string.default_description_text)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingCircle(size = 200.dp, strokeWidthPercentage = 0.1f)
                Spacer(Modifier.height(15.dp))
            }
        }

    }
}