package es.uji.smallaris.model.integration


import es.uji.smallaris.model.CalculadorRutasORS
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.RepositorioRutas
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioORS
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import androidx.test.platform.app.InstrumentationRegistry
import es.uji.smallaris.model.ProxyPrecios
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.VehicleException
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
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
            val mockResponse = readFileFromAssets("car_route.txt")
            coEvery { mockServicioORS.getRuta(any(), any(), any(), any()) } returns mockResponse
            coEvery { mockServicioPrecio.getPrecioCombustible(any(), any()) } returns PRECIO_CARBURANTE
            coEvery { mockServicioPrecio.getPrecioElectrico() } returns PRECIO_ELECTRICO

            // Inicializar ServicioAPIs y asignar mocks
            servicioAPIs = ServicioAPIs
            servicioAPIs.setServicioMapa(mockServicioORS)
            servicioAPIs.setServicioPrecios(mockServicioPrecio)

        }

        private fun readFileFromAssets(fileName: String): String {
            val context = InstrumentationRegistry.getInstrumentation().context
            return context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
    }

    @Before
    fun setUp() {
        mockRepositorioRutas = mockk(relaxed = true)
        coEvery { mockRepositorioRutas.addRuta(any()) } returns true
        coEvery { mockRepositorioRutas.enFuncionamiento() } returns true
    }

    @Test
    fun addRuta_R4HU01_R4HU04_calcularYGuardarRuta_mockObtenerRutaTrayecto_mockFirebase() =
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
    fun addRuta_R4HU05_GuardarRutaYaGuardada_mockFirebase() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
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
        coVerify { mockRepositorioRutas.addRuta(any()) }
    }

    @Test
    fun builder_R4HU01_trayectoFaltaVehiculo_mockFirebase_mockObtenerRutaTrayecto() = runBlocking {

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
        coVerify { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    @Test
    fun builder_R4HU02_costeCarburanteCorrecto_mockFirebase_mockObtenerRutaTrayecto_mockCoste() =
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
    fun builder_R4HU02_costeElectricoCorrecto_mockFirebase_mockObtenerRutaTrayecto_mockCoste() =
        runBlocking {
            // Given
            val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Electrico)
            val servicioRutas =
                ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

            // When
            val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()

            // Then
            val costeEsperado = (ruta.getDistancia() / 100) * coche.consumo * (PRECIO_ELECTRICO / 1000)
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

    private fun redondear(valor: Float): Double {
        val factor = 10.0.pow(4.0)
        return Math.round(valor * factor) / factor
    }
}