package es.uji.smallaris.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier

@Composable
inline fun < reified E: Enum<E>>EnumDropDown(
    modifier: Modifier = Modifier,
    opciones: List<E> = enumValues<E>().toList(),
    elegida: MutableState<E>
) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableStateOf(opciones.indexOf(elegida.value))
    }

    val tiposVehiculo = opciones

    Column(
        modifier= modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isDropDownExpanded.value = true
                }
            ) {
                Text(text = tiposVehiculo[itemPosition.value].name)
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
                tiposVehiculo.forEachIndexed { index, username ->
                    DropdownMenuItem(text = {
                        Text(text = username.name)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.value = index
                            elegida.value = tiposVehiculo[itemPosition.value]
                        })
                }
            }
        }

    }
}