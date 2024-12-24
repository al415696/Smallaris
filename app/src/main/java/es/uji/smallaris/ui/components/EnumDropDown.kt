package es.uji.smallaris.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
        mutableIntStateOf(opciones.indexOf(elegida.value))
    }

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
                Text(modifier= Modifier.fillMaxWidth().weight(1f),
                    text = opciones[itemPosition.intValue].name)
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
                        Text(text = elemento.name)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.intValue = index
                            elegida.value = opciones[itemPosition.intValue]
                        })
                }
            }
        }

    }
}