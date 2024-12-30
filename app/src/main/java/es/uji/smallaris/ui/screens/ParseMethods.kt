package es.uji.smallaris.ui.screens

import es.uji.smallaris.model.ArquetipoVehiculo
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

//String a Double con precaunciones
fun String.safeToDouble(): Double {
    val formatter = NumberFormat.getInstance(Locale.getDefault())
    if (this.isEmpty() || this == "-")
        return 0.0

    try {
        val result = formatter.parse(this)?.toDouble()
        return result?: 0.0
    } catch (e: NumberFormatException) {
        return 0.0 // Retorna un valor predeterminado si la conversión falla
    }catch (e: ParseException) {
        return 0.0 // Retorna un valor predeterminado si la conversión falla
    }
}
//Métodos para formatear Dobles a Strings
fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) {
//        String.format(Locale.US,"%.0f", this)
        String.format(Locale.getDefault(),"%.0f", this)
    } else {
        String.format(Locale.getDefault(),"%f", this).trimEnd('0')
    }
}
fun Double.toCleanString(numOfDecimals: Int = 0): String {
    return if (this % 1.0 == 0.0) {
//        String.format(Locale.US,"%.0f", this)
        String.format(Locale.getDefault(),"%.0f", this)
    } else {
        String.format(Locale.getDefault(),"%."+numOfDecimals+"f", this).trimEnd('0')
    }
}

//Metodos para mostrar información al usuario
fun Double.toCleanCost(arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Combustible): String {
    var unidad: String = ""
    if (arquetipoVehiculo == ArquetipoVehiculo.Otro)
        unidad = "cal"
    else
        unidad = "€"

    return String.format(Locale.getDefault(),"%.2f", this) +" $unidad"
}
fun Float.toCleanDistance(): String {
    return if (this < 1)
        String.format(Locale.getDefault(),"%.0f", this*1000) + " m"
    else
        String.format(Locale.getDefault(),"%.3f", this) + " km"
}
fun Float.toTimeFormat(): String{
    val horas: Int = (this / 60).toInt()
    val minutos: Int = (this % 60).toInt()
    val segundos: Int = (this * 60).toInt()
    return if (horas == 0){
        if (minutos == 0)
            String.format(Locale.getDefault(), "%d s", segundos)
        else
            String.format(Locale.getDefault(), "%d min", minutos)
    }
    else
        String.format(Locale.getDefault(), "%d h %d m", horas, minutos)
}