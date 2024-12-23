package es.uji.smallaris.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@SuppressLint("UnrememberedMutableState")
@Composable
inline fun <E>ListDropDown(
    modifier: Modifier = Modifier,
    opciones: List<E> = emptyList(),
    elegida: MutableState<E>,
    crossinline shownValue: (objeto: E) -> String = { objeto-> objeto.toString()}
) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }
    val ready by remember{ derivedStateOf { elegida.value != null }}

    val itemPosition = remember { mutableIntStateOf ( opciones.indexOf(elegida.value) ) }
//    val itemPosition = remember {
//        mutableIntStateOf(opciones.indexOf(elegida.value))
//    }
    if (!ready) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                text = shownValue(elegida.value),
                maxLines = 3)

                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "DropDown Icon"
                )

        }
    }
    else {
        itemPosition.intValue = opciones.indexOf(elegida.value)
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = elegida.value != null) {
                        isDropDownExpanded.value = true
                    }
                ) {
                    Text(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                        text = shownValue(opciones[itemPosition.intValue]),
                        maxLines = 3)
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "DropDown Icon"
                        )
                }

                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }) {
                    opciones.forEachIndexed { index, elemento ->
                        DropdownMenuItem(text = {
                            Text(text = shownValue(elemento))
                        },
                            onClick = {
                                isDropDownExpanded.value = false
                                itemPosition.value = index
                                elegida.value = opciones[itemPosition.value]
                            })
                    }
                }
            }

        }
    }
}
@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviewListDropDownCorrecto(){

    Surface {
        ListDropDown(opciones = listOf("Hola"),
            elegida = mutableStateOf("Hola"),
            shownValue = { objeto: String -> objeto.toString() })
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviewListDropDownEsperando(){

    Surface {
        ListDropDown(opciones = emptyList(),
            elegida = mutableStateOf(null),
            shownValue = {objeto: String? ->  objeto ?: "Cargando..." })
    }
}