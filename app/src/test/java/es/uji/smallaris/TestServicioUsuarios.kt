package es.uji.smallaris

import es.uji.smallaris.model.RepositorioUsuarios
import es.uji.smallaris.model.ServicioUsuarios
import es.uji.smallaris.model.UnloggedUserException
import es.uji.smallaris.model.UnregisteredUserException
import es.uji.smallaris.model.UserAlreadyExistsException
import es.uji.smallaris.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test


class TestServicioUsuarios {

    companion object {

        private val mockRepositorioUsuarios = mockk<RepositorioUsuarios>(relaxed = true)
        private lateinit var servicioUsuarios: ServicioUsuarios

        @JvmStatic
        @BeforeClass
        fun setup() {
            coEvery { mockRepositorioUsuarios.enFuncionamiento() } returns true
            coEvery {
                mockRepositorioUsuarios.registrarUsuario(
                    eq("registrarUser@uji.es"),
                    any()
                )
            } throws UserAlreadyExistsException()
            coEvery {
                mockRepositorioUsuarios.registrarUsuario(
                    eq("registrarUser2@uji.es"),
                    any()
                )
            } returns Usuario(correo = "registrarUser2@uji.es")
            coEvery {
                mockRepositorioUsuarios.iniciarSesion(
                    eq("registrarUser@uji.es"),
                    any()
                )
            } returns Usuario(correo = "registrarUser@uji.es")
            coEvery {
                mockRepositorioUsuarios.iniciarSesion(
                    eq("registrarUser2@uji.es"),
                    any()
                )
            } throws UnregisteredUserException("El usuario no está registrado")
            coEvery { mockRepositorioUsuarios.cerrarSesion() } returns Usuario(correo = "registrarUser@uji.es")
        }
    }

    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioExito() {
        runBlocking {
            // Dado
            servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)

            // Cuando
            val usuario =
                servicioUsuarios.registrarUsuario("registrarUser2@uji.es", "alHugo415617")
            println(usuario)

            // Entonces
            assertEquals(Usuario(correo = "registrarUser2@uji.es"), usuario)
            coVerify { mockRepositorioUsuarios.enFuncionamiento() }
            coVerify { mockRepositorioUsuarios.registrarUsuario(any(), any()) }
        }
    }

    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
        runBlocking {

            var resultado: UserAlreadyExistsException? = null

            // Dado
            servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)

            // Cuando
            try {
                servicioUsuarios.registrarUsuario("registrarUser@uji.es", "12345678")
            } catch (excepcion: UserAlreadyExistsException) {
                resultado = excepcion
            }

            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UserAlreadyExistsException)
            coVerify { mockRepositorioUsuarios.enFuncionamiento() }
            coVerify { mockRepositorioUsuarios.registrarUsuario(any(), any()) }
        }
    }

    @Test
    fun iniciarSesion_R1HU02_iniciarSesionExito() {
        runBlocking {

            // Dado
            servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)

            // Cuando
            val usuario = servicioUsuarios.iniciarSesion("registrarUser@uji.es", "12345678")

            // Entonces
            assertEquals(Usuario(correo = "registrarUser@uji.es"), usuario)
            assertNotNull(servicioUsuarios.obtenerUsuarioActual())
            coVerify { mockRepositorioUsuarios.enFuncionamiento() }
            coVerify { mockRepositorioUsuarios.iniciarSesion(any(), any()) }
        }
    }

    @Test
    fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {

        var resultado: UnregisteredUserException? = null

        // Dado
        servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)

        // Cuando
        try {
            servicioUsuarios.iniciarSesion("registrarUser2@uji.es", "alHugo415617")
        } catch (excepcion: UnregisteredUserException) {
            resultado = excepcion
        }

        // Entonces
        assertNotNull(resultado)
        assertTrue(resultado is UnregisteredUserException)
        coVerify { mockRepositorioUsuarios.enFuncionamiento() }
        coVerify { mockRepositorioUsuarios.iniciarSesion(any(), any()) }
    }

    @Test
    fun cerrarSesion_R1HU03_cerrarSesionExito() {
        runBlocking {
            // Dado
            servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)
            servicioUsuarios.iniciarSesion("registrarUser@uji.es", "12345678")

            // Cuando
            val resultado = servicioUsuarios.cerrarSesion()

            // Entonces
            assertEquals("registrarUser@uji.es", resultado.correo)
            coVerify { mockRepositorioUsuarios.enFuncionamiento() }
            coVerify { mockRepositorioUsuarios.cerrarSesion() }
        }
    }

    @Test
    fun cerrarSesion_R1HU03_cerrarSesionSinIniciarSesion() {
        runBlocking {

            val mockRepositorioUsuarios = mockk<RepositorioUsuarios>(relaxed = true)
            coEvery { mockRepositorioUsuarios.cerrarSesion() } throws UnloggedUserException()
            coEvery { mockRepositorioUsuarios.enFuncionamiento() } returns true

            var resultado: UnloggedUserException? = null

            // Dado
            val servicioUsuarios = ServicioUsuarios(mockRepositorioUsuarios)
            // Cuando
            try {
                servicioUsuarios.cerrarSesion() // Intentar cerrar sesión sin usuario
            } catch (excepcion: UnloggedUserException) {
                resultado = excepcion
            }
            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UnloggedUserException)
            coVerify { mockRepositorioUsuarios.enFuncionamiento() }
            coVerify { mockRepositorioUsuarios.cerrarSesion() }
        }
    }
}
