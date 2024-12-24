package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import java.util.Locale
import kotlin.text.*

class ProxyPrecios : IServicioPrecios {
    private val cacheCarburante: MutableMap<String, Combustible> = mutableMapOf()
    private var cacheElectrico: Electricidad = Electricidad(0.0, 0)
    private val servicioReal: ServicioPrecio = ServicioPrecio()

    private val TTL_CARBURANTE = 30 * 60 * 1000L // 30 minutos en milisegundos
    private val TTL_ELECTRICIDAD = 24 * 60 * 60 * 1000L // 1 día en milisegundos

    override suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Double {
        val combustible = getCombustibleFromCacheOrFetch(lugar, tipoVehiculo)
        return combustible[TipoVehiculo.Gasolina95]
    }

    override suspend fun getPrecioElectrico(): Double {
        return getElectricidadFromCacheOrFetch()
    }

    // Método para gestionar caché o actualizar precios de combustibles
    private suspend fun getCombustibleFromCacheOrFetch(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Combustible {
//        val cacheKey = "${lugar.latitud},${lugar.longitud}"
        val cacheKey = String.format(locale = Locale.US,"%.5f", lugar.latitud) + "," + String.format(locale = Locale.US,"%.5f", lugar.longitud) + "," + tipoVehiculo.toString()
        val currentTime = System.currentTimeMillis()

        val cached = cacheCarburante[cacheKey]
        if (cached != null && (currentTime - cached.timestamp) <= TTL_CARBURANTE) {
            return cached
        }

        // Si no está en caché o ha caducado, obtener los datos del servicio real
        val fetchedCombustible = servicioReal.getPrecioCombustible(lugar)

        // Actualizar la caché
        cacheCarburante[cacheKey] = fetchedCombustible

        return fetchedCombustible
    }

    // Método para gestionar caché o actualizar precio de electricidad
    private suspend fun getElectricidadFromCacheOrFetch(): Double {
        val cacheKey = "globalElectricidad"
        val currentTime = System.currentTimeMillis()

        val cached = cacheElectrico
        if (cached.precio == 0.0 && (currentTime - cached.timestamp) <= TTL_ELECTRICIDAD) {
            return cached.precio
        }

        // Si no está en caché o ha caducado, obtener los datos del servicio real
        val fetchedElectricidad = servicioReal.getPrecioElecticidad()
        val precio = fetchedElectricidad

        // Actualizar la caché
        cacheElectrico = Electricidad(
            precio = precio,
            timestamp = currentTime
        )

        return precio
    }
}