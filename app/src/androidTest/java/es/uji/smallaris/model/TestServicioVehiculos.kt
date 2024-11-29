package es.uji.smallaris.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioVehiculos {
    @Test
    fun addVehiculo_R3HU1V1_anyadirVehiculoListaVaciaOk(){
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)

        //      WHEN
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        //      THEN
        assertEquals(vehiculo, Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ))
        assertEquals(true, servicioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, servicioVehiculos.getVehiculos().count())

    }
    @Test
    fun addVehiculo_R3HU1I1_anyadirVehiculoconMismoYaEnLista(){
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)


        //      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        }
        //THEN
        catch (excepcion: Exception){

            assertEquals(VehicleAlredyExistsException::class.java, excepcion ::class.java)
        }
    }

    @Test
    fun getVehiculo_R3HU2V1_getListaCon1Vehiculo(){
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
//        WHEN
        var lista = servicioVehiculos.getVehiculos()
//        THEN
        assertEquals(1, lista.count())
        assertEquals(Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ), lista[0])
    }

    @Test
    fun getVehiculo_R3HU2I1_getListaErrorConexion(){
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
        var resultado : Exception? = null
//        WHEN
        try {
            servicioVehiculos.getVehiculos()
        }
        catch (exception: Exception){
            resultado = exception
        }
//        THEN
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)

    }

}