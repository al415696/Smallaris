package es.uji.smallaris.model

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioRutas {

    @Test
    fun addRuta_R4HU01_R4HU04_calcularRutaCortaOK() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))

        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
        assert(servicioRutas.getRutas().size == 1)
    }

    @Test
    fun addRuta_R4HU04_calcularRutaEconomicaOK() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))

        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
        assert(servicioRutas.getRutas().size == 1)
    }

    @Test
    fun addRuta_R4HU01_trayectoFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))

        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        try {
            servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino)
                .setTipo(TipoRuta.Corta).buildAndSave()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
        assertEquals(0, servicioRutas.getRutas().size)
    }

    @Test
    fun addRuta_R4HU02_costeCocheCorrecto() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getCoste() > 0)
        assertEquals(1, servicioRutas.getRutas().size)
    }

    @Test
    fun addRuta_R4HU02_costeFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        try {
            servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino)
                .setTipo(TipoRuta.Corta).buildAndSave()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
        assertEquals(0, servicioRutas.getRutas().size)
    }

    @Test
    fun addRuta_R4HU3_costePieCorrecto() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val pie = Vehiculo("Pie", matricula = "Pie", tipo = TipoVehiculo.Pie)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(pie)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getCoste() > 0)
        assertEquals(1, servicioRutas.getRutas().size)
    }

    @Test
    fun addRuta_R4HU3_costeBiciCorrecto() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        val ruta = servicioRutas.build().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(pie)
            .setTipo(TipoRuta.Corta).buildAndSave()

        // Then
        assert(ruta.getCoste() > 0)
        assertEquals(1, servicioRutas.getRutas().size)
    }

    @Test
    fun addRuta_R4HU3_rutaPieExcepcion() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val pie = Vehiculo("Pie", matricula = "Pie", tipo = TipoVehiculo.Pie)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino =
            LugarInteres(39.34651, -0.35293, "Albufera de Valencia, Valencia, España", "Valencia")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        try {
            servicioRutas.build().setNombre("Ruta hacia Albufera").setInicio(origen).setFin(destino).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).buildAndSave()
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
        assertEquals(0, servicioRutas.getRutas().size)

    }

    @Test
    fun addRuta_R4HU3_rutaBiciExcepcion() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino =
            LugarInteres(39.34651, -0.35293, "Albufera de Valencia, Valencia, España", "Valencia")
        val servicioRutas = ServicioRutas(CalculadorRutasORS())

        // When
        try {
            servicioRutas.build().setNombre("Ruta hacia Albufera").setInicio(origen).setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).buildAndSave()
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
        assertEquals(0, servicioRutas.getRutas().size)

    }
}