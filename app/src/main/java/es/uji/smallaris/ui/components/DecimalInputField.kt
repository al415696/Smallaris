package es.uji.smallaris.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormatSymbols

@Composable
fun DecimalInputField(
    modifier: Modifier = Modifier,
    decimalFormatter: IDecimalFormatter = StandardDecimalFormatter(),
    text: MutableState<String>,
    maxLenght: Int = 8,
    useVisualTransformation: Boolean = true,
    label: @Composable (() -> Unit)? = null,

    ) {

//    var text by remember {
//        mutableStateOf(0.0)
//    }

    TextField(
        modifier = modifier,
        value = text.value,
        onValueChange = {
            if (it.length <= maxLenght)
                text.value = decimalFormatter.cleanup(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
        ),
        visualTransformation = if (useVisualTransformation) DecimalInputVisualTransformation(decimalFormatter) else VisualTransformation.None,
        label = label
    )
}
abstract class IDecimalFormatter(
    val symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
){


    abstract fun cleanup(input: String): String
    abstract fun formatForVisual(input: String): String
}

class StandardDecimalFormatter : IDecimalFormatter() {

    private val thousandsSeparator = symbols.groupingSeparator
    private val decimalSeparator = symbols.decimalSeparator

    override fun cleanup(input: String): String {

        if (input.matches("\\D".toRegex())) return ""
        if (input.matches("0+".toRegex())) return "0"

        val sb = StringBuilder()

        var hasDecimalSep = false

        for (char in input) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
            }
        }

        return sb.toString()
    }

    override fun formatForVisual(input: String): String {

        val split = input.split(decimalSeparator)

        val intPart = split[0]
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()

        val fractionPart = split.getOrNull(1)

        return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
    } }

class CoordinateDecimalFormatter : IDecimalFormatter() {

    private val thousandsSeparator = symbols.groupingSeparator
    private val decimalSeparator = symbols.decimalSeparator

    override fun cleanup(input: String): String {
        println(input)
        if (input.startsWith('-')) {
            if (input.length > 1 && input.substring(1).matches("\\D".toRegex()))
                return ""

        }
        else
            if (input.matches("\\D".toRegex())) { return ""
            }
        if (input.matches("0+".toRegex())) return "0"
        if (input.matches("-0+".toRegex())) return "-0"

        if (input.matches("-?\\d{3}[^.]".toRegex())) return input.substring(0,input.length-1)

        val sb = StringBuilder()

        var hasDecimalSep = false
        var hasNegative = false


        for (char in input) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == '-' && sb.isEmpty() && !hasNegative){
                sb.append(char)
                hasNegative = true
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
                continue
            }

        }

        return sb.toString()
    }

    override fun formatForVisual(input: String): String {

        val split = input.split(decimalSeparator)

        val intPart = split[0]
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()

        val fractionPart = split.getOrNull(1)

        return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
    }
}

private class DecimalInputVisualTransformation(
    private val decimalFormatter: IDecimalFormatter
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val inputText = text.text
        val formattedNumber = decimalFormatter.formatForVisual(inputText)

        val newText = AnnotatedString(
            text = formattedNumber,
            spanStyles = text.spanStyles,
            paragraphStyles = text.paragraphStyles
        )

        val offsetMapping = FixedCursorOffsetMapping(
            contentLength = inputText.length,
            formattedContentLength = formattedNumber.length
        )

        return TransformedText(newText, offsetMapping)
    }
}

private class FixedCursorOffsetMapping(
    private val contentLength: Int,
    private val formattedContentLength: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = formattedContentLength
    override fun transformedToOriginal(offset: Int): Int = contentLength
}