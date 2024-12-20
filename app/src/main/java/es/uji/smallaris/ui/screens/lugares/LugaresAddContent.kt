package es.uji.smallaris.ui.screens.lugares

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.LoadingCircle

@Composable
fun LugaresAddContent(
    funAddLugar: suspend (longitud: Double, latitud: Double, nombre: String) -> String,
    onBack: () -> Unit = {}
) {
    var nombre = remember { mutableStateOf("") }
    var nombreValid = remember { mutableStateOf(false) }
    var longitud = remember { mutableStateOf("") }
    var latitud = remember { mutableStateOf("") }

    var confirmadoAdd by remember { mutableStateOf(false) }


    var mensajeError by remember { mutableStateOf("") }
    var errorConAdd by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }
    if (confirmadoAdd) {
        LaunchedEffect(Unit) {
            mensajeError = funAddLugar(
                if (longitud.value.isEmpty()) 0.0 else longitud.value.toDouble(),
                if (latitud.value.isEmpty()) 0.0 else latitud.value.toDouble(),
                nombre.value,
            )
            confirmadoAdd = false
            errorConAdd = mensajeError.isNotEmpty()
            if (!errorConAdd)
                onBack()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()

        ) {
            Surface(modifier = Modifier,
            color= MaterialTheme.colorScheme.secondary) {
                Row(
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {

                    IconButton(onClick = onBack, modifier = Modifier.size(75.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.default_description_text),
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(15.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(45.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Nombre
                FilteredTextField(
                    text = nombre,
                    valid = nombreValid,
                    filter = { input ->
                        if (input.isEmpty())
                            "Tiene que tener un nombre"
                        else
                            ""
                    },
                    label = "Nombre del método de transporte"
                )

                if (confirmadoAdd) {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Añadiendo...",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(15.dp))
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
                if (errorConAdd)
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = mensajeError,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

            }


            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
            ) {
//                Surface(color = MaterialTheme.colorScheme.primaryContainer) {// Submit Button
                Button(
                    modifier = Modifier.fillMaxSize(),
                    enabled = nombreValid.value,
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onSurface,
                    ),
                    onClick = {
                        // Handle form submission
                        confirmadoAdd = true
                    }) {
                    Text(text="Añadir",
                        style = MaterialTheme.typography.headlineLarge)
                }
//                }
            }
        }
    }
}


@Preview
@Composable
private fun previewLugaresInteresAddContent() {
    LugaresAddContent(funAddLugar = {_,_,_-> "" })
}