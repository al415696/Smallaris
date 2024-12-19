package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FilteredTextField(
    text: MutableState<String>,
    valid: MutableState<Boolean>,
    filter: (input: String) -> String = {input -> ""},
    maxLength: Int = 100,
    label: String = ""

){
    var errorMessage: String by remember { mutableStateOf("") }
    errorMessage = filter(text.value)
    Surface (shape = MaterialTheme.shapes.small) {
        Column()
        {
            TextField(
                value = text.value,
                onValueChange = {
                    errorMessage = filter(it)
                    println(errorMessage)
                    valid.value = errorMessage.isEmpty()
                    if (it.length <= maxLength)
                        text.value = it
                },
                label = { Text(label) }
            )
            if (!valid.value) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

}