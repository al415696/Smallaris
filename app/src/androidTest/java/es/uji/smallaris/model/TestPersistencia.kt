package es.uji.smallaris.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestPersistencia {

    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioVehiculos: ServicioVehiculos
    private lateinit var servicioLugares: ServicioLugares
    private lateinit var servicioRutas: ServicioRutas

    @Before
    fun iniciarServicios() = runBlocking {
        val repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("al415647@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS())
    }

    @After
    fun limpiarUsuario() = runBlocking {

    }
    
    @Test
    fun testPersistenciaVehiculo() = runBlocking {
        // Dado
        val vehiculosAntes = servicioVehiculos.getVehiculos()
        assertTrue(vehiculosAntes.isEmpty())

        val nombreVehiculo = "VehiculoTestNuevo"
        val consumo = 12.0
        val matricula = "TEST9876"
        val tipo = TipoVehiculo.Gasolina95

        // Cuando
        val vehiculoCreado = servicioVehiculos.addVehiculo(nombreVehiculo, consumo, matricula, tipo)
        servicioUsuarios.cerrarSesion()

        iniciarServicios()

        // Entonces
        val vehiculosRecuperados = servicioVehiculos.getVehiculos()
        assertTrue(vehiculosRecuperados.contains(vehiculoCreado))
    }

/*
    @Test
    fun testPersistenciaLugar() = runBlocking {
        // Dado
        iniciarServicios()
        assert(ServicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
        limpiarUsuario()

        val longitud = 2.0
        val latitud = 41.0
        val nombre = "LugarTest"

        // Cuando
        val lugarCreado = servicioLugares.addLugar(longitud, latitud, nombre)
        servicioUsuarios.cerrarSesion()

        iniciarServicios()

        servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
        val lugaresRecuperados = servicioLugares.getLugares()
        val lugarEncontrado = lugaresRecuperados.find {
            it.nombre == lugarCreado.nombre && it.latitud == lugarCreado.latitud && it.longitud == lugarCreado.longitud
        }

        // Entonces
        assertNotNull(lugarEncontrado)
        assertEquals(lugarCreado.nombre, lugarEncontrado?.nombre)
        assertEquals(lugarCreado.latitud, lugarEncontrado?.latitud)
        assertEquals(lugarCreado.longitud, lugarEncontrado?.longitud)
    }


    @Test
    fun testPersistenciaRuta() = runBlocking {
        // Dado
        iniciarServicios()
        assert(ServicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        assert(ServicioAPIs.apiEnFuncionamiento(API.RUTA))
        servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

        val lugarInicio = LugarInteres(longitud = 2.0, latitud = 41.0, nombre = "LugarInicio", municipio = "TestCiudad")
        val lugarFin = LugarInteres(longitud = 2.5, latitud = 41.5, nombre = "LugarFin", municipio = "TestCiudad")

        val vehiculo = Vehiculo(nombre = "VehiculoTest", consumo = 10.0, matricula = "TEST9876", tipo = TipoVehiculo.Gasolina95)

        val ruta = Ruta(
            inicio = lugarInicio,
            fin = lugarFin,
            vehiculo = vehiculo,
            tipo = TipoRuta.Rapida,
            trayecto = LineString.fromLngLats(listOf(Point.fromLngLat(2.0, 41.0), Point.fromLngLat(2.5, 41.5))),
            distancia = 10f,
            duracion = 30f,
            coste = 5.0,
            nombre = "RutaTest"
        )

        // Cuando
        val rutaCreada = servicioRutas.addRuta(ruta)
        servicioUsuarios.cerrarSesion()

        // Reiniciar servicios después de cerrar sesión
        iniciarServicios()

        servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
        val rutasRecuperadas = servicioRutas.getRutas()
        val rutaEncontrada = rutasRecuperadas.find {
            it.getNombre() == rutaCreada.getNombre() && it.getInicio() == rutaCreada.getInicio() && it.getFin() == rutaCreada.getFin()
        }

        // Verificaciones finales
        assertNotNull(rutaEncontrada)
        assertEquals(rutaCreada.getDistancia(), rutaEncontrada?.getDistancia())
        assertEquals(rutaCreada.getDuracion(), rutaEncontrada?.getDuracion())
        assertEquals(rutaCreada.getCoste(), rutaEncontrada?.getCoste())
    }
*/
}
