package es.uji.smallaris.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.mapbox.maps.extension.style.expressions.dsl.generated.mod

@Composable
fun FilteredTextField(
    modifier: Modifier = Modifier,
    text: MutableState<String>,
    valid: MutableState<Boolean>,
    filter: (input: String) -> String = {input -> ""},
    maxLength: Int = 100,
    visualTransformation : VisualTransformation = VisualTransformation.None,
    label: String = ""

){
    var errorMessage: String by remember { mutableStateOf(filter(text.value)) }
    errorMessage = filter(text.value)
    valid.value = errorMessage.isEmpty()
    Surface (modifier= modifier, shape = MaterialTheme.shapes.small) {
        Column(verticalArrangement = Arrangement.Center)
        {
            TextField(
                modifier = modifier,
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
//                    modifier = modifier,
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewFilteredTextField(){
    Column {
        FilteredTextField(
            text = mutableStateOf(""),
            valid =  mutableStateOf(false)
        )

        var test by remember{ mutableStateOf("test")}
        TextField(
            shape= MaterialTheme.shapes.small,
            value = test,
            onValueChange = {
                test = it
            },
            supportingText = {
                Surface {
                    Text(text = "Prueba", color = MaterialTheme.colorScheme.error)
                }
                             },

            )
    }
}