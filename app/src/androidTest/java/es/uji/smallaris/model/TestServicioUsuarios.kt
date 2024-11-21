package es.uji.smallaris.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TestServicioUsuarios {

    @Test
    fun registrarUsuario_R1HU01V1() {
        // Dado
        val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
        val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
        // Cuando
        val usuario = servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
        // Entonces
        assertEquals(Usuario(uid = "", correo = "al415617@uji.es", preferencias = null), usuario)
    }

    @Test
    fun registrarUsuario_R1HU01I1() {
        // Dado
        val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
        val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
        servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
        // Cuando
        try {
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
        }
        // Entonces
        catch (excepcion: Exception){
            assertEquals(UserAlreadyExistsException::class.java, excepcion ::class.java)
        }
    }

    @Test
    fun iniciarSesion_R1HU02V1() {
        // Dado
        val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
        val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
        servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
        // Cuando
        val usuario = servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
        // Entonces
        assertEquals(Usuario(uid = "", correo = "al415617@uji.es", preferencias = null), usuario)
    }

    @Test
    fun iniciarSesion_R1HU02I1() {
        // Dado
        val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
        val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
        // Cuando
        try {
            servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
        }
        // Entonces
        catch (excepcion: Exception){
            assertEquals(UnregisteredUserException::class.java, excepcion ::class.java)
        }
    }
}