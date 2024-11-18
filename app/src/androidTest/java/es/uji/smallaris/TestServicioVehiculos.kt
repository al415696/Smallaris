package es.uji.smallaris

import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.RepositorioVehiculos
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.VehicleAlredyExistsException
import es.uji.smallaris.model.Vehiculo
import org.junit.Assert.assertEquals
import org.junit.Test

class TestServicioVehiculos {
    @Test
    fun addVehiculo_R3HU1V1(){
//      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()

        servicioVehiculos.setRepositorio(repositorioVehiculos)
//      WHEN
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

//      THEN
        assertEquals(vehiculo, Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ))
        assertEquals(true, repositorioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, repositorioVehiculos.getVehiculos().count())

    }
    @Test
    fun addVehiculo_R3HU1I1(){
//      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()
        servicioVehiculos.setRepositorio(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)


//      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        }
//                  THEN
        catch (excepcion: Exception){

            assertEquals(VehicleAlredyExistsException::class.java, excepcion ::class.java)
        }
    }


}