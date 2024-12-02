package es.uji.smallaris.model

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioRutas {

    @Test
    fun addRuta_R4HU01_calcularRutaOK() {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))

        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina)
        val origen =
            LugarInteres(-0.067893F, 39.991907F, "Talleres, Castellón de la Plana, VC, España")
        val destino = LugarInteres(0.013474F, 39.971408F, "Cámara de tráfico 10, Grao, VC, España")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
        assert(servicioRutas.getRutas().size == 1)
    }

    @Test
    fun addRuta_R4HU01_faltaVehiculo() {

        var resultado: VehicleException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))

        val origen =
            LugarInteres(-0.067893F, 39.991907F, "Talleres, Castellón de la Plana, VC, España")
        val destino = LugarInteres(0.013474F, 39.971408F, "Cámara de tráfico 10, Grao, VC, España")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        try {
            servicioRutas.build().setInicio(origen).setFin(destino)
                .setTipo(TipoRuta.Corta).buildAndSave()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
        assertEquals(0, servicioRutas.getRutas().size)
    }
}