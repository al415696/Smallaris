package es.uji.smallaris.model

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
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, VC, España")
        var resultadoPeticion95 = 0.0
        var resultadoPeticion98 = 0.0
        var resultadoPeticionDiesel = 0.0
        // When
        try {
            resultadoPeticion95 = servicioPrecio.getPrecioCombustible(origen).gasolina95
            resultadoPeticion98 = servicioPrecio.getPrecioCombustible(origen).gasolina98
            resultadoPeticionDiesel = servicioPrecio.getPrecioCombustible(origen).diesel
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
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, VC, España")
        var resultadoPeticionElec = 0F
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