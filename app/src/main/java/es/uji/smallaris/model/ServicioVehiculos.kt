package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

class ServicioVehiculos(private val repositorio: RepositorioVehiculos) {

    private var vehiculos = mutableListOf<Vehiculo>()

    // Función suspendida para initializer los vehículos
    suspend fun updateVehiculos() {
        if (repositorio.enFuncionamiento()) {
            vehiculos = repositorio.getVehiculos().toMutableList()
        } else {
            throw ConnectionErrorException("Firebase no está disponible")
        }
    }

    init {
        runBlocking {
            updateVehiculos()
        }
    }

    @Throws(VehicleException::class, ConnectionErrorException::class)
    suspend fun addVehiculo (nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Vehiculo {
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        val vehiculo: Vehiculo
        //        Checks de validez de datos tienen que estar aquí no en las clases que use
        if (checkValidezVehiculo(nombre, consumo, matricula, tipo)){
            vehiculo = Vehiculo(nombre = nombre, consumo = consumo, matricula = matricula, tipo = tipo)

            // Revisa si el nombre y la matricula son originales, excepción si no
            checkUnicidadVehiculo(nombre, matricula)

            // Se ejecuta el método add del repositorio
            if (repositorio.addVehiculos(vehiculo)){
                vehiculos.add(vehiculo)
            }else{
                throw VehicleException("No se pudo añadir el vehiculo por un problema remoto")
            }
            return vehiculo
        }else{
            throw VehicleException("Datos no válidos para un vehiculo")
        }
//        return null
    }
    private fun checkValidezVehiculo(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Boolean{
        // Hay un nombre, una matriculo, y el consumo no es negativo
        return nombre.isNotEmpty() && matricula.isNotEmpty() && consumo >=0
    }
    private fun checkUnicidadVehiculo(nombre: String, matricula: String, vehiculoIgnorado: Vehiculo? = null){
        var nombreRep: Boolean
        var matriculaRep: Boolean
        for (otro in vehiculos){
            nombreRep = (nombre == otro.nombre)
            matriculaRep = (matricula == otro.matricula)
            if (nombreRep || matriculaRep){
                var errorMessage = StringBuilder("Vehiculo con ")
                if (nombreRep){
                    errorMessage.append("nombre \"$nombre\" ")
                    if (matriculaRep)
                        errorMessage.append("y matricula \"$matricula\" ")
                }else
                    errorMessage.append("matricula \"$matricula\" ")
                errorMessage.append("ya existe")
                if (vehiculoIgnorado == null || otro != vehiculoIgnorado)
                    throw VehicleException(errorMessage.toString())
            }
        }
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getVehiculos(ordenVehiculos: OrdenVehiculo = OrdenVehiculo.FAVORITO_THEN_NOMBRE): List<Vehiculo>{
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")

        return vehiculos.sortedWith(
            ordenVehiculos.comparator()
        )
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getVehiculo(nombre: String, matricula: String): Vehiculo? {
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        for (otro in vehiculos){
            if (nombre == otro.nombre && matricula == otro.matricula){
                return otro
            }
        }
        return null
    }
    @Throws(VehicleException::class, ConnectionErrorException::class)
    suspend fun updateVehiculo(viejo: Vehiculo,
                               nuevoNombre: String = viejo.nombre,
                               nuevoConsumo: Double = viejo.consumo,
                               nuevaMatricula: String = viejo.matricula,
                               nuevoTipoVehiculo: TipoVehiculo = viejo.tipo
                               ) : Boolean{
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        val indexViejo = vehiculos.indexOf(viejo)
        if (indexViejo != -1 || !checkValidezVehiculo(nuevoNombre,nuevoConsumo,nuevaMatricula,nuevoTipoVehiculo)) {
            val nuevoVehiculo = Vehiculo(nuevoNombre,nuevoConsumo,nuevaMatricula,nuevoTipoVehiculo)
            // Si no se está cambiando nada, anula la operación
            if (nuevoVehiculo == viejo)
                throw VehicleException("No se está modificando ningún dato")
            //Lanza excepción si los nuevos atributos causan conflictos
            checkUnicidadVehiculo(nuevoNombre,nuevaMatricula, viejo)

            nuevoVehiculo.setFavorito(viejo.isFavorito())

            if ( repositorio.updateVehiculos(viejo, nuevoVehiculo)){
                vehiculos[indexViejo] = nuevoVehiculo
                return true
            }

        }
        return false
    }

    @Throws(ConnectionErrorException::class, VehicleException::class)
    suspend fun deleteVehiculo(vehiculo: Vehiculo): Boolean{
        if(vehiculo.isFavorito())
            throw VehicleException("No se puede eliminar un vehiculo favorito")
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (vehiculos.contains(vehiculo)){
            return if(repositorio.removeVehiculo(vehiculo)){
                vehiculos.remove(vehiculo)
            }else{
                false
            }
        }else{
            throw VehicleException("Se ha intentado eliminar un vehiculo no existente")
        }

    }

    @Throws(ConnectionErrorException::class)
    suspend fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean = true): Boolean{
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (vehiculo.isFavorito() == favorito)
            return false
        if (vehiculos.contains(vehiculo)){
            vehiculo.setFavorito(favorito)
            repositorio.setVehiculoFavorito(vehiculo, favorito)
            return true
        }
        return false
    }

    companion object{
        private lateinit var servicio: ServicioVehiculos
        fun getInstance(): ServicioVehiculos{
            if (!this::servicio.isInitialized){
                servicio = ServicioVehiculos(repositorio = RepositorioFirebase.getInstance())
            }
            return servicio
        }
    }
}