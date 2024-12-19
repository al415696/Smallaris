package es.uji.smallaris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uji.smallaris.ui.components.LoadingCircle
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(loadingProcess: suspend ()->Unit, onTimeout: () -> Unit){
    Surface(modifier = Modifier.fillMaxSize(),
        ) {
        val currentOnTimeout by rememberUpdatedState(onTimeout)
        LaunchedEffect(Unit) {
            //Procesos de carga en los viewModel
            loadingProcess()


            currentOnTimeout()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            LoadingCircle(totalWidth =  100.dp)
            Spacer(Modifier.height(100.dp))

//            Icon(
//                imageVector = Icons.Filled.Build,
//                contentDescription = "Cargando",
//                Modifier.size(100.dp)
//            )

        }

    }
}