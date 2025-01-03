package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestPersistencia {

    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioVehiculos: ServicioVehiculos
    private lateinit var servicioLugares: ServicioLugares
    private lateinit var servicioRutas: ServicioRutas
    private lateinit var calculadorRutas: CalculadorRutasORS
    private lateinit var servicioAPIs: ServicioAPIs

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("testPersistencia@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("testPersistencia@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        calculadorRutas = CalculadorRutasORS(servicioAPIs)
        servicioRutas = ServicioRutas(calculadorRutas, repositorioFirebase, servicioAPIs)
    }

    @After
    fun tearDown() {
        runBlocking {
            try {
                servicioUsuarios.borrarUsuario()
            } catch (e: Exception) {
                println("Error al borrar el usuario: ${e.message}")
            }
        }
    }

    @Test
    fun testPersistenciaVehiculo() = runBlocking {
        val nombreVehiculo = "VehiculoTestNuevo"
        val consumo = 12.0
        val matricula = "TEST9876"
        val tipo = TipoVehiculo.Gasolina95

        val vehiculoCreado = servicioVehiculos.addVehiculo(nombreVehiculo, consumo, matricula, tipo)

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("testPersistencia@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        val vehiculosRecuperados = servicioVehiculos.getVehiculos()
        assertTrue(vehiculosRecuperados.contains(vehiculoCreado))

        val vehiculoFavorito =
            vehiculoCreado.let { servicioVehiculos.setVehiculoFavorito(it, true) }
        assertTrue(vehiculoFavorito)
        assertTrue(vehiculoCreado.isFavorito())

        servicioVehiculos.setVehiculoFavorito(vehiculoCreado, false)

        val vehiculoEliminado = vehiculoCreado.let { servicioVehiculos.deleteVehiculo(it) }
        assertTrue(vehiculoEliminado)

        val vehiculosRecuperadosPostEliminacion = servicioVehiculos.getVehiculos()
        assertFalse(vehiculosRecuperadosPostEliminacion.contains(vehiculoCreado))
    }

    @Test
    fun testPersistenciaLugar() = runBlocking {
        val longitud = -0.12345
        val latitud = 39.98765
        val nombreLugar = "LugarTestNuevo"

        val lugarCreado = servicioLugares.addLugar(longitud, latitud, nombreLugar)

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("testPersistencia@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        val lugaresRecuperados = servicioLugares.getLugares()
        assertTrue(lugaresRecuperados.contains(lugarCreado))

        val lugarHechoFavorito = servicioLugares.setLugarInteresFavorito(lugarCreado, true)
        assertTrue(lugarHechoFavorito)
        assertTrue(lugarCreado.isFavorito())

        servicioLugares.setLugarInteresFavorito(lugarCreado, false)

        val lugarEliminado = servicioLugares.deleteLugar(lugarCreado)
        assertTrue(lugarEliminado)

        val lugaresRecuperadosPostEliminacion = servicioLugares.getLugares()
        assertFalse(lugaresRecuperadosPostEliminacion.contains(lugarCreado))
    }

    @Test
    fun testPersistenciaRuta() = runBlocking {
        val origen = LugarInteres(-0.12345, 39.98765, "OrigenTest", "MunicipioOrigen")
        val destino = LugarInteres(-0.54321, 39.56789, "DestinoTest", "MunicipioDestino")
        val vehiculo = Vehiculo("Coche", 5.0, "ABC123", TipoVehiculo.Gasolina95)
        val tipoRuta = TipoRuta.Corta

        val builderWrapper = RutaBuilderWrapper(calculadorRutas, servicioAPIs, servicioRutas)
        builderWrapper
            .setInicio(origen)
            .setFin(destino)
            .setVehiculo(vehiculo)
            .setTipo(tipoRuta)
            .setNombre("RutaTest")

        val rutaCreada = builderWrapper.build()

        servicioRutas.addRuta(rutaCreada)
        assertTrue(servicioRutas.contains(rutaCreada))

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("testPersistencia@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        calculadorRutas = CalculadorRutasORS(servicioAPIs)
        servicioRutas = ServicioRutas(calculadorRutas, repositorioFirebase, servicioAPIs)

        val rutasRecuperadas = servicioRutas.getRutas()
        assertTrue(rutasRecuperadas.contains(rutaCreada))

        val rutaFavorita = servicioRutas.setFavorito(rutaCreada, true)
        assertTrue(rutaFavorita)
        assertTrue(rutaCreada.isFavorito())

        servicioRutas.setFavorito(rutaCreada, false)

        val rutaEliminada = servicioRutas.deleteRuta(rutaCreada)
        assertTrue(rutaEliminada)

        assertFalse(servicioRutas.contains(rutaCreada))
    }
}
