package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class TestServicioUsuarios {

    class PruebasConBeforeTest {
        private lateinit var repositorioFirebase: RepositorioFirebase
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Before
        fun setUp() {
            repositorioFirebase = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioFirebase)

            runBlocking {
                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                    servicioUsuarios.cerrarSesion()
                } catch (_: UserAlreadyExistsException) {
                }
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                try {
                    servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
                    servicioUsuarios.borrarUsuario()
                } catch (e: Exception) {
                    println("Error al borrar el usuario: ${e.message}")
                }
            }
        }

        @Test
        fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
            runBlocking {
                // Given
                var resultado: UserAlreadyExistsException? = null

                // When
                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                } catch (excepcion: UserAlreadyExistsException) {
                    resultado = excepcion
                }

                // Then
                assertNotNull(resultado)
                assertTrue(resultado is UserAlreadyExistsException)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionExito() {
            runBlocking {
                // Given
                val correo = "al415647@uji.es"
                val contrasena = "12345678"

                // When
                val usuario = servicioUsuarios.iniciarSesion(correo, contrasena)

                // Then
                assertEquals(Usuario(correo = correo), usuario)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {
            // Given
            var resultado: UnregisteredUserException? = null

            // When
            try {
                servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            } catch (excepcion: UnregisteredUserException) {
                resultado = excepcion
            }

            // Then
            assertNotNull(resultado)
            assertTrue(resultado is UnregisteredUserException)
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionExito() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

            // When
            val usuario = servicioUsuarios.cerrarSesion()

            // Then
            assertEquals("al415647@uji.es", usuario.correo)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionSinIniciarSesion() = runBlocking {
            // Given
            var resultado: UnloggedUserException? = null
            val repositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

            // When
            try {
                servicioUsuarios.cerrarSesion()
            } catch (excepcion: UnloggedUserException) {
                resultado = excepcion
            }

            // Then
            assertNotNull(resultado)
            assertTrue(resultado is UnloggedUserException)
        }

        @Test
        fun cambiarContrasena_R1HU05_cambiarContrasenaExito() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val contrasenaNueva = "87654321"

            // When
            val resultado = servicioUsuarios.cambiarContrasena("12345678", contrasenaNueva)

            // Then
            assertTrue(resultado)

            servicioUsuarios.cerrarSesion()
            val usuario = servicioUsuarios.iniciarSesion("al415647@uji.es", contrasenaNueva)
            servicioUsuarios.cambiarContrasena(contrasenaNueva, "12345678") // Vuelvo a dejar la contraseña previa para que funcione el After
            assertEquals(Usuario(correo = "al415647@uji.es"), usuario)
        }

        @Test
        fun cambiarContrasena_R1HU05_cambiarContrasenaNuevaContrasenaInvalida() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            var resultado: InvalidPasswordException? = null

            // When
            try {
                servicioUsuarios.cambiarContrasena("12345678", "123")
            } catch (excepcion: InvalidPasswordException) {
                resultado = excepcion
            }

            // Then
            assertNotNull(resultado)
            assertTrue(resultado is InvalidPasswordException)
        }

        @Test
        fun establecerTipoRutaPorDefecto_R5HU02_establecerTipoRutaPorDefectoExito() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val tipoRutaElegido = TipoRuta.Rapida

            // When
            val resultado = servicioUsuarios.establecerTipoRutaPorDefecto(tipoRutaElegido)

            // Then
            assertTrue(resultado)
            val tipoRutaPorDefecto = servicioUsuarios.obtenerTipoRutaPorDefecto()
            assertEquals(tipoRutaElegido, tipoRutaPorDefecto)
        }

        @Test
        fun establecerTipoRutaPorDefecto_R5HU02_establecerTipoRutaPorDefectoYaEstablecido() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val tipoRutaElegido = TipoRuta.Rapida
            servicioUsuarios.establecerTipoRutaPorDefecto(tipoRutaElegido)

            // When
            var resultado: RouteException? = null
            try {
                servicioUsuarios.establecerTipoRutaPorDefecto(tipoRutaElegido)
            } catch (excepcion: RouteException) {
                resultado = excepcion
            }

            // Then
            assertNotNull(resultado)
            assertTrue(resultado is RouteException)
        }

        @Test
        fun establecerVehiculoPorDefecto_R5HU01_establecerVehiculoPorDefectoExito() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val vehiculoCreado = Vehiculo(
                nombre = "VehiculoTest",
                consumo = 10.0,
                matricula = "TEST1234",
                tipo = TipoVehiculo.Diesel
            )

            // When
            val resultado = servicioUsuarios.establecerVehiculoPorDefecto(vehiculoCreado)

            // Then
            assertTrue(resultado)
            val vehiculoPorDefecto = servicioUsuarios.obtenerVehiculoPorDefecto()
            assertEquals(vehiculoCreado, vehiculoPorDefecto)
        }

        @Test
        fun establecerVehiculoPorDefecto_R5HU01_establecerVehiculoPorDefectoYaEstablecido() = runBlocking {
            // Given
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val vehiculoCreado = Vehiculo(
                nombre = "VehiculoTest",
                consumo = 10.0,
                matricula = "TEST1234",
                tipo = TipoVehiculo.Diesel
            )
            servicioUsuarios.establecerVehiculoPorDefecto(vehiculoCreado)

            // When
            var resultado: VehicleException? = null
            try {
                servicioUsuarios.establecerVehiculoPorDefecto(vehiculoCreado)
            } catch (excepcion: VehicleException) {
                resultado = excepcion
            }

            // Then
            assertNotNull(resultado)
            assertTrue(resultado is VehicleException)
        }
    }

    class RegistrarUsuarioExitoTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Test
        fun registrarUsuario_R1HU01_registrarUsuarioExito() {
            runBlocking {
                // Given
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

                // When
                val usuario = servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")

                // Then
                assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                // Given
                try {
                    servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")

                    // When
                    servicioUsuarios.borrarUsuario()
                } catch (e: Exception) {
                    println("Error al borrar el usuario: ${e.message}")
                }
            }
        }
    }

    class BorrarUsuarioExitoTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Test
        fun borrarUsuario_R1HU04_borrarUsuarioExito() = runBlocking {
            // Given
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")

            // When
            val usuario = servicioUsuarios.borrarUsuario()

            // Then
            assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }
    }
}
