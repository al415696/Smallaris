package es.uji.smallaris.model.integration


import es.uji.smallaris.model.CalculadorRutasORS
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.RepositorioRutas
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioORS
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import androidx.test.platform.app.InstrumentationRegistry
import es.uji.smallaris.model.RouteException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import kotlin.math.pow


class TestServicioRutas {

    companion object {
        private lateinit var mockRepositorioRutas: RepositorioRutas
        private lateinit var mockServicioORS: ServicioORS
        private lateinit var servicioAPIs: ServicioAPIs

        @JvmStatic
        @BeforeClass
        fun setUpAll() {
            // Inicializar mocks una vez
            mockRepositorioRutas = mockk(relaxed = true)
            mockServicioORS = mockk(relaxed = true)

            // Configurar respuestas de los mocks
            val mockResponse = readFileFromAssets("car_route.txt")
            every { mockServicioORS.getRuta(any(), any(), any(), any()) } returns mockResponse

            // Inicializar ServicioAPIs y asignar mocks
            servicioAPIs = ServicioAPIs
            servicioAPIs.setServicioMapa(mockServicioORS)
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
    fun addRuta_R4HU01_R4HU04_calcularYGuardarRuta_mockObtenerRutaTrayecto_mockFirebase() = runBlocking {

        println(mockRepositorioRutas.enFuncionamiento())

        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")

        val servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // Then
        assertTrue("La distancia no es correcta. Obtenida: ${ruta.getDistancia()}",redondear(ruta.getDistancia(), 4) == redondear(7.9913F, 4))
        assertTrue("La duración no es correcta. Obtenida: ${ruta.getDuracion()}",redondear(ruta.getDuracion(), 4) == redondear(1148.6F / 60, 4))
        assert(servicioRutas.getRutas().size == 1)
        verify { mockServicioORS.getRuta(any(), any(), any(), any()) }
        coVerify { mockRepositorioRutas.addRuta(any()) }
    }

    @Test
    fun addRuta_R4HU05_GuardarRutaCortaYaGuardada_mock() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")
        val servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), mockRepositorioRutas, servicioAPIs)
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // Then
        assertTrue("La distancia no es correcta. Obtenida: ${ruta.getDistancia()}",redondear(ruta.getDistancia(), 4) == redondear(7.9913F, 4))
        assertTrue("La duración no es correcta. Obtenida: ${ruta.getDuracion()}",redondear(ruta.getDuracion(), 4) == redondear(1148.6F / 60, 4))
        // When
        try {
            servicioRutas.addRuta(ruta)
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
        assertTrue("Se tienen ${servicioRutas.getRutas().size} rutas guardadas", servicioRutas.getRutas().size == 1)
    }

    private fun redondear(valor: Float, decimales: Int): Double {
        val factor = 10.0.pow(decimales.toDouble())
        return Math.round(valor * factor) / factor
    }

}