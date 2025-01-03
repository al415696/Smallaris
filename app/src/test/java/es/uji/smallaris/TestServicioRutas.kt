package es.uji.smallaris

import es.uji.smallaris.model.CalculadorRutasORS
import es.uji.smallaris.model.ProxyPrecios
import es.uji.smallaris.model.RepositorioRutas
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioORS
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.UbicationException
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

class TestServicioRutas {

    companion object {

        private const val PRECIO_CARBURANTE = 10.0
        private const val PRECIO_ELECTRICO = 5000.0

        private lateinit var mockRepositorioRutas: RepositorioRutas
        private lateinit var mockServicioORS: ServicioORS
        private lateinit var servicioAPIs: ServicioAPIs
        private lateinit var mockServicioPrecio: ProxyPrecios
        private val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        private val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        private val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)

        @JvmStatic
        @BeforeClass
        fun setUpAll() {
            // Inicializar mocks una vez
            mockRepositorioRutas = mockk(relaxed = true)
            mockServicioORS = mockk(relaxed = true)
            mockServicioPrecio = mockk(relaxed = true)

            // Configurar respuestas de los mocks
            val mockResponse = readFileFromResources("car_route.txt")
            coEvery { mockServicioORS.getRuta(any(), any(), any(), any()) } returns mockResponse
            coEvery {
                mockServicioPrecio.getPrecioCombustible(
                    any(),
                    any()
                )
            } returns PRECIO_CARBURANTE
            coEvery { mockServicioPrecio.getPrecioElectrico() } returns PRECIO_ELECTRICO

            coEvery { mockRepositorioRutas.enFuncionamiento() } returns true
            coEvery { mockRepositorioRutas.addRuta(any()) } returns true
            coEvery { mockRepositorioRutas.setRutaFavorita(any(), any()) } returns true
            coEvery { mockRepositorioRutas.deleteRuta(any()) } returns true

            // Inicializar ServicioAPIs y asignar mocks
            servicioAPIs = ServicioAPIs
            servicioAPIs.setServicioMapa(mockServicioORS)
            servicioAPIs.setServicioPrecios(mockServicioPrecio)

        }

        private fun readFileFromResources(fileName: String): String {
            // Obtén la ruta del archivo en la carpeta resources
            val filePath = javaClass.classLoader?.getResource(fileName)?.toURI()
                ?: throw IllegalArgumentException("File not found in resources")


            // Lee el contenido del archivo
            return Files.readAllLines(Paths.get(filePath)).joinToString("\n")
        }
    }

    @After
    fun setup() {
        clearMocks(
            mockRepositorioRutas,
            mockServicioORS,
            mockServicioPrecio,
            recordedCalls = true,
            answers = false
        )
    }

    @Test
    fun addRuta_R4HU01_R4HU04_R4HU05_calcularYGuardarRuta() =
        runBlocking {
            // Given
            val servicioRutas =
                ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

            // When
            val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()

            servicioRutas.addRuta(ruta)

            // Then
            assertTrue(
                "La distancia no es correcta. Obtenida: ${ruta.getDistancia()}",
                redondear(ruta.getDistancia()) == redondear(
                    7.9913F
                )
            )
            assertTrue(
                "La duración no es correcta. Obtenida: ${ruta.getDuracion()}",
                redondear(ruta.getDuracion()) == redondear(
                    1148.6F / 60
                )
            )

            assert(servicioRutas.getRutas().size == 1)
            coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
            coVerify { mockRepositorioRutas.addRuta(any()) }
        }

    @Test
    fun addRuta_R4HU05_GuardarRutaYaGuardada() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // When
        try {
            servicioRutas.addRuta(ruta)
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
        assertTrue(
            "Se tienen ${servicioRutas.getRutas().size} rutas guardadas",
            servicioRutas.getRutas().size == 1
        )
        coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockRepositorioRutas.addRuta(any()) }
    }

    @Test
    fun builder_R4HU01_trayectoFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
        coVerify(exactly = 0) { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun builder_R4HU04_calcularRutaCortaFaltaDestino() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: UbicationException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationException)
        coVerify(exactly = 0) { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun builder_R4HU02_costeCarburanteCorrecto() =
        runBlocking {
            // Given
            val servicioRutas =
                ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

            // When
            val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()

            // Then
            val costeEsperado = (ruta.getDistancia() / 100) * coche.consumo * PRECIO_CARBURANTE
            assertTrue(
                "El coste no es correcto. Obtenido: ${
                    redondear(
                        ruta.getCoste().toFloat()
                    )
                } vs esperado: ${redondear(costeEsperado.toFloat())}",
                redondear(ruta.getCoste().toFloat()) == redondear(costeEsperado.toFloat())
            )
            coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
            coVerify { mockServicioPrecio.getPrecioCombustible(any(), any()) }
        }

    @Test
    fun builder_R4HU02_costeElectricoCorrecto() =
        runBlocking {
            // Given
            val cocheElectrico = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Electrico)
            val servicioRutas =
                ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

            // When
            val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(cocheElectrico)
                .setTipo(TipoRuta.Corta).build()

            // Then
            val costeEsperado =
                (ruta.getDistancia() / 100) * coche.consumo * (PRECIO_ELECTRICO / 1000)
            assertTrue(
                "El coste no es correcto. Obtenido: ${
                    redondear(
                        ruta.getCoste().toFloat()
                    )
                } vs esperado: ${redondear(costeEsperado.toFloat())}",
                redondear(ruta.getCoste().toFloat()) == redondear(costeEsperado.toFloat())
            )
            coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
            coVerify { mockServicioPrecio.getPrecioElectrico() }
        }

    @Test
    fun builder_R4HU02_costeFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
        coVerify(exactly = 0) { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun builder_R4HU3_costePieCorrecto() = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        val pie = Vehiculo("Pie", matricula = "Pie", tipo = TipoVehiculo.Pie)

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(pie)
            .setTipo(TipoRuta.Corta).build()

        // Then
        val costeEsperado = ruta.getDistancia() * pie.consumo
        assertTrue(
            "El coste no es correcto. Obtenido: ${
                redondear(
                    ruta.getCoste().toFloat()
                )
            } vs esperado: ${redondear(costeEsperado.toFloat())}",
            redondear(ruta.getCoste().toFloat()) == redondear(costeEsperado.toFloat())
        )
        coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun builder_R4HU3_costeBiciCorrecto(): Unit = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(bici)
            .setTipo(TipoRuta.Corta).build()

        // Then
        val costeEsperado = ruta.getDistancia() * bici.consumo
        assertTrue(
            "El coste no es correcto. Obtenido: ${
                redondear(
                    ruta.getCoste().toFloat()
                )
            } vs esperado: ${redondear(costeEsperado.toFloat())}",
            redondear(ruta.getCoste().toFloat()) == redondear(costeEsperado.toFloat())
        )
        coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun getLugares_R4HU6_listaRutasCorrecto(): Unit = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)

        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).build()
        )

        // When
        val listaRutas = servicioRutas.getRutas()

        // Then
        assert(listaRutas.size == 1)
        coVerify { mockRepositorioRutas.enFuncionamiento() }
    }

    @Test
    fun getLugares_R5HU5_listaRutasFavoritoPrimero(): Unit = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)

        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por corta").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).build()
        )
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón rapida").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Rapida).build()
        ).let {
            servicioRutas.setFavorito(it, true)
        }

        // When
        val listaRutas = servicioRutas.getRutas()

        // Then
        assert(listaRutas[0].getNombre() == "Ruta por Castellón rapida")
        coVerify { mockRepositorioRutas.setRutaFavorita(any(), any()) }
    }

    @Test
    fun getLugares_setFavoritos_R5HU5V1_asignarRutaNoFavoritaComoFavorita(): Unit = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)

        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón corta").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).build()
        )

        // When
        val listaRutas = servicioRutas.getRutas()
        val cambiado = servicioRutas.setFavorito(listaRutas[0], true)

        // Then
        assertTrue(listaRutas[0].isFavorito())
        assertTrue(cambiado)
        coVerify { mockRepositorioRutas.setRutaFavorita(any(), any()) }
    }

    @Test
    fun getLugares_setFavoritos_R5HU5I1_asignarRutaFavoritaComoFavorita(): Unit = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)

        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón2").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).build()
        ).let {
            servicioRutas.setFavorito(it, true)
        }

        // When
        val listaRutas = servicioRutas.getRutas()
        val cambiado = servicioRutas.setFavorito(listaRutas[0], true)

        // Then
        assertTrue(listaRutas[0].isFavorito())
        assertFalse(cambiado)
        coVerify(exactly = 1) { mockRepositorioRutas.setRutaFavorita(any(), any()) }
    }

    @Test
    fun deleteRuta_R4HU07_eliminarRutaOK() = runBlocking {
        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        val ruta = servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        )

        // When
        val resultado = servicioRutas.deleteRuta(ruta)

        //Then
        assertEquals(true, resultado)
        assertEquals(0, servicioRutas.getRutas().size)
        coVerify { mockRepositorioRutas.deleteRuta(any()) }
    }

    @Test
    fun deleteRuta_R4HU07_eliminarRutaFavorita() = runBlocking {

        var excepcion: RouteException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        val ruta = servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        )
        servicioRutas.setFavorito(ruta, true)

        // When
        try {
            servicioRutas.deleteRuta(ruta)
        } catch (e: RouteException) {
            excepcion = e
        }

        //Then
        assertNotNull(excepcion)
        assertTrue(excepcion is RouteException)
        assertTrue(excepcion!!.message.equals("Ruta favorita no se puede borrar"))
        assertEquals(1, servicioRutas.getRutas().size)
        coVerify(exactly = 0) { mockRepositorioRutas.deleteRuta(any()) }
    }


    private fun redondear(valor: Float): Double {
        val factor = 10.0.pow(4.0)
        return Math.round(valor * factor) / factor
    }
}