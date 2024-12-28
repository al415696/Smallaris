package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorBubble(
    errorText: MutableState<String>
) {
    if (errorText.value.isNotEmpty())
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            contentColor = MaterialTheme.colorScheme.error,
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                modifier = Modifier.padding(15.dp),
                text = errorText.value,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
}