package es.uji.smallaris.model

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.jvm.Throws


class ServicioPrecio{

    private val valueForNotPresent = -1.0 // valor negativo
    private val servicioPrecioCombustible: IServicioPrecioCombustible = ServicioPrecioCombustible(valueForNotPresent)
    private val servicioPrecioElectricidad: IServicioPrecioElectricidad = ServicioPrecioElectricidad(valueForNotPresent)

    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo? = null): Combustible {
        return try {
            servicioPrecioCombustible.getClosestCarburante(lugar = lugar, spcopeBusqueda = ScopePeticionAPI.Municipal, tipoVehiculo= tipoVehiculo)
        } catch (e: APIException) {
            servicioPrecioCombustible.getClosestCarburante(lugar = lugar, spcopeBusqueda = ScopePeticionAPI.Nacional, tipoVehiculo= tipoVehiculo)
        }
    }
    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getPrecioElecticidad(): Double {
        return servicioPrecioElectricidad.obtenerPrecioMedioElecHoy().precio
    }
}




