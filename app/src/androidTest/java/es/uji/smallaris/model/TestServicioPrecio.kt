package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TestServicioPrecio {

    @Test
    fun getPrecioGasolina_R4HU02_costeTodosCarburantesCastellon() = runBlocking {

        var excepcion: Exception? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))
        val servicioPrecio = ServicioPrecio()
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        var resultadoPeticion95 = 0.0
        var resultadoPeticion98 = 0.0
        var resultadoPeticionDiesel = 0.0
        // When
        try {
            resultadoPeticion95 = servicioPrecio.getPrecioCombustible(
                origen,
                TipoVehiculo.Gasolina95
            )[TipoVehiculo.Gasolina95]
            resultadoPeticion98 = servicioPrecio.getPrecioCombustible(
                origen,
                TipoVehiculo.Gasolina98
            )[TipoVehiculo.Gasolina98]
            resultadoPeticionDiesel = servicioPrecio.getPrecioCombustible(
                origen,
                TipoVehiculo.Diesel
            )[TipoVehiculo.Diesel]
        } catch (e: Exception) {
            excepcion = e
        }

        // Then
        assertNull(excepcion)
        assertNotEquals(resultadoPeticion95, 0.0)
        assertNotEquals(resultadoPeticion98, 0.0)
        assertNotEquals(resultadoPeticionDiesel, 0.0)
    }

    @Test
    fun getPrecioElectricidad_R4HU02_costeElectricidadHoy() = runBlocking {

        var excepcion: Exception? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))
        val servicioPrecio = ServicioPrecio()
        var resultadoPeticionElec = 0.0
        // When
        try {
            resultadoPeticionElec = servicioPrecio.getPrecioElecticidad()
        } catch (e: Exception) {
            excepcion = e
        }

        // Then
        assertNull(excepcion)
        assertNotEquals(resultadoPeticionElec, 0.0)
    }
}