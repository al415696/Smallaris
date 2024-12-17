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
import org.junit.Before
import org.junit.Test
import androidx.test.platform.app.InstrumentationRegistry
import es.uji.smallaris.model.RepositorioFirebase
import io.mockk.verify
import org.junit.Assert.assertTrue


class TestServicioRutas {


    private var repositorioRutas: RepositorioRutas = mockk<RepositorioRutas>(relaxed = true)
    private var mockServicioORS = mockk<ServicioORS>(relaxed = true)
    private val servicioAPIs = ServicioAPIs

    @Before
    fun setUp() {
        repositorioRutas = RepositorioFirebase()
        mockServicioORS = mockk<ServicioORS>(relaxed = true)
        servicioAPIs.setServicioMapa(mockServicioORS)
    }

    @Test
    fun addRuta_R4HU01_R4HU04_calcularYGuardarRutaCortaOK_() = runBlocking {

        val mockResponse = readFileFromAssets("car_route.txt")
        every { mockServicioORS.getRuta(any(), any(), any(), any()) } returns mockResponse

        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(-0.067893, 39.991907, "Talleres, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana")
        val destino = LugarInteres(0.013474, 39.971408, "Cámara de tráfico 10, Grao, Comunidad Valenciana, España", "Castellón de la Plana")

        val servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioRutas, servicioAPIs)

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen).setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // Then
        assertTrue("La distancia no es correcta. Obtenida: ${ruta.getDistancia()}",redondear(ruta.getDistancia(), 4) == redondear(7.9913F, 4))
        assertTrue("La duración no es correcta. Obtenida: ${ruta.getDuracion()}",redondear(ruta.getDuracion(), 4) == redondear(1148.6F / 60, 4))
        assert(servicioRutas.getRutas().size == 1)
        verify { mockServicioORS.getRuta(any(), any(), any(), any()) }
    }

    private fun readFileFromAssets(fileName: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun redondear(valor: Float, decimales: Int): Double {
        val factor = Math.pow(10.0, decimales.toDouble())
        return Math.round(valor * factor) / factor
    }
}