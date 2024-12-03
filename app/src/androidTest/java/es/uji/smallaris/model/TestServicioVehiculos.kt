package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioVehiculos {
    @Test
    fun addVehiculo_R3HU1V1_anyadirVehiculoListaVaciaOk() = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)

        //      WHEN
        val vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        //      THEN
        assertEquals(vehiculo, Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ))
        assertEquals(true, servicioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, servicioVehiculos.getVehiculos().count())

    }
    @Test
    fun addVehiculo_R3HU1I1_anyadirVehiculoconMismoYaEnLista(): Unit = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
        var resultado : Exception? = null


        //      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        }
        //THEN
        catch (excepcion: Exception){
            resultado = excepcion
        }
        assertTrue(resultado is VehicleAlredyExistsException)
    }

    @Test
    fun getVehiculos_R3HU2V1_getListaCon1Vehiculo() = runBlocking{
//        GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
            val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
//        WHEN
        val lista = servicioVehiculos.getVehiculos()
//        THEN
        assertEquals(1, lista.count())
        assertEquals(Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ), lista[0])
    }

    @Test
    fun getVehiculos_R3HU2I1_getListaErrorConexion() = runBlocking{
//        GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
        var resultado : Exception? = null
//        WHEN
        try {
            servicioVehiculos.getVehiculos()
        }

//        THEN
        catch (exception: Exception){
            resultado = exception
        }
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)

    }

    @Test
    fun getVehiculos_R5HU4V2_getVehiculosOrdenadosFavoritosPrimero() = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zulom",consumo=5.13, matricula = "3333WWW" ,tipo=TipoVehiculo.Diesel)
        servicioVehiculos.addVehiculo(nombre= "Abobamasnow",consumo=1.36, matricula = "1234DPP" ,tipo=TipoVehiculo.Gasolina)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            ?.let { servicioVehiculos.setFavorito(it) }
        servicioVehiculos.addVehiculo(nombre= "Carrozaso",consumo=15.82, matricula = "5675BFC" ,tipo=TipoVehiculo.Gasolina)


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()

        //      THEN
        assertEquals("Zyxcrieg", lista[0].nombre)
        assertEquals("Abobamasnow", lista[1].nombre)
        assertEquals("Zulom", lista[lista.size-1].nombre)

    }
    @Test
    fun getVehiculo_setFavorito_R5HU4V1_asignarVehiculoNoFavoritoComoFavorito() = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()
        val cambiado = servicioVehiculos.setFavorito(lista[0])

        //      THEN
        assertTrue(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
    }
    @Test
    fun getVehiculo_setFavorito_R5HU4I1_asignarVehiculoYaFavoritoComoFavorito() = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            ?.let { servicioVehiculos.setFavorito(it) }
        servicioVehiculos.getVehiculo(nombre = "Zyxcrieg", matricula = "4444XXX" )?.setFavorito(true)


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()
        val cambiado = servicioVehiculos.setFavorito(lista[0])

        //      THEN
        assertFalse(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
    }
}