package es.uji.smallaris.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import es.uji.smallaris.ui.state.VehiculosViewModel

@Composable
fun VehiculosScreen(
    viewModel: VehiculosViewModel,
    testFunction: () -> Unit
){
    Surface {

        Column {
            Text("se supone que es Vehiculos")
            Text("test: " + viewModel.cosaVehiculos)
            Button(onClick = testFunction) {}
        }
    }
}