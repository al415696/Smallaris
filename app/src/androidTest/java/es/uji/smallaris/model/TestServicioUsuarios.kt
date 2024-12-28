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

    class PruebasSinAfterPropioTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Before
        fun setUp() {
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
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
                var resultado: UserAlreadyExistsException? = null

                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                } catch (excepcion: UserAlreadyExistsException) {
                    resultado = excepcion
                }

                assertNotNull(resultado)
                assertTrue(resultado is UserAlreadyExistsException)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionExito() {
            runBlocking {
                val usuario = servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
                assertEquals(Usuario(correo = "al415647@uji.es"), usuario)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {
            var resultado: UnregisteredUserException? = null

            try {
                servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            } catch (excepcion: UnregisteredUserException) {
                resultado = excepcion
            }

            assertNotNull(resultado)
            assertTrue(resultado is UnregisteredUserException)
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionExito() = runBlocking {
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
            val usuario = servicioUsuarios.cerrarSesion()
            assertEquals("al415647@uji.es", usuario.correo)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionSinIniciarSesion() = runBlocking {
            var resultado: UnloggedUserException? = null
            val repositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

            try {
                servicioUsuarios.cerrarSesion()
            } catch (excepcion: UnloggedUserException) {
                resultado = excepcion
            }

            assertNotNull(resultado)
            assertTrue(resultado is UnloggedUserException)
        }
    }

    class RegistrarUsuarioExitoTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Test
        fun registrarUsuario_R1HU01_registrarUsuarioExito() {
            runBlocking {
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                val usuario = servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
                assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                try {
                    servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
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
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")

            val usuario = servicioUsuarios.borrarUsuario()

            assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }
    }
}