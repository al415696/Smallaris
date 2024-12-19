package es.uji.smallaris.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DeleteAlertDialogue(
    shouldShowDialog: MutableState<Boolean>,
    deleteFuncition:suspend () -> Unit,
    nombreObjetoBorrado: String = "El objeto seleccionado"
) {
    var confirmadoBorrado by remember{ mutableStateOf(false) }
    if(confirmadoBorrado)
        LaunchedEffect(Unit) {
            deleteFuncition()
            shouldShowDialog.value = false
        }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },

            title = { Text(text = "¿Seguro que quieres borrar?") },
            text = {
                if (confirmadoBorrado) {
                    Column {
                        Text(text = "Borrando...")
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
                else
                    Text(text = "$nombreObjetoBorrado se borrará permantentemente")
            },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    onClick = {
                        confirmadoBorrado = true
                    }
                ) {
                    Text(
                        text = "Borrar",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        )
    }
}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun previewAlertDialogue(){
    DeleteAlertDialogue(
        shouldShowDialog =  mutableStateOf(true),
        deleteFuncition =  {}

    )
}