package es.uji.smallaris.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import es.uji.smallaris.ui.state.MapaViewModel

@Composable
fun MapaScreen(
    viewModel: MapaViewModel
){
    Surface {
        Text("se supone que es Mapa")
    }
}