package es.uji.smallaris.model

import kotlin.jvm.Throws

class ServicioVehiculos(private val repositorio: RepositorioVehiculos) {

    private val vehiculos = mutableListOf<Vehiculo>()

    init {
        this.vehiculos.addAll(repositorio.getVehiculos())
    }

    @Throws(VehicleAlredyExistsException::class, ConnectionErrorException::class)
    suspend fun addVehiculo (nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Vehiculo? {
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
                return vehiculo
            }
        }
        return null
    }
    private fun checkValidezVehiculo(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Boolean{
        // Hay un nombre, una matriculo, y el consumo no es negativo
        return nombre.isNotEmpty() && matricula.isNotEmpty() && consumo >=0
    }
    private fun checkUnicidadVehiculo(nombre: String,  matricula: String){
        var nombreRep: Boolean
        var matriculaRep: Boolean
        for (otro in vehiculos){
            nombreRep = (nombre == otro.nombre)
            matriculaRep = (matricula == otro.matricula)
            if (nombreRep || matriculaRep){
                var errorMessage = StringBuilder("Vehiculo con ")
                if (nombreRep){
                    errorMessage.append("nombre \"$nombre\"")
                    if (matriculaRep)
                        errorMessage.append("y matricula \"$matricula\"")
                }else
                    errorMessage.append("matricula \"$matricula\"")
                errorMessage.append(" ya existe")
                throw VehicleAlredyExistsException(errorMessage.toString())
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

    @Throws(ConnectionErrorException::class, VehicleException::class)
    suspend fun deleteVehiculo(vehiculo: Vehiculo): Boolean{
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (vehiculos.contains(vehiculo)){
            return if(! repositorio.removeVehiculo(vehiculo)){
                vehiculos.remove(vehiculo)
            }else{
                false
            }
        }else{
            throw VehicleException("Se ha intentado eliminar un vehiculo no existente")
        }

    }

    @Throws(ConnectionErrorException::class)
    suspend fun setFavorito(vehiculo: Vehiculo, favorito: Boolean = true): Boolean{
        if ( !repositorio.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (vehiculo.isFavorito() == favorito)
            return false
        vehiculo.setFavorito(favorito)
        if (vehiculos.contains(vehiculo)){
            repositorio.setVehiculoFavorito(vehiculo, favorito)
            return true
        }
        return false
    }


}