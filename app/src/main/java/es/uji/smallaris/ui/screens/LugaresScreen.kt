package es.uji.smallaris.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import es.uji.smallaris.ui.state.LugaresViewModel

@Composable
fun LugaresScreen(
    viewModel: LugaresViewModel
){
    Surface {
        Text("se supone que es Lugares")
    }
}