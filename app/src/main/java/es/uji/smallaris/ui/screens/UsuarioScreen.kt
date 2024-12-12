package es.uji.smallaris.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import es.uji.smallaris.ui.state.UsuarioViewModel

@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel
){
    Surface {
        Text("se supone que es Usuario")
    }
}