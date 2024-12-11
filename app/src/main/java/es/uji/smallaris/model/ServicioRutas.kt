package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking

class ServicioRutas(private val calculadorRutas: CalculadorRutas) {

    private val repositorioRutas: RepositorioRutas = RepositorioFirebase()
    private val rutas = mutableListOf<Ruta>()

    init {
        runBlocking {
            inicializarRutas()
        }
    }

    private suspend fun inicializarRutas() {
        this.rutas.addAll(repositorioRutas.getRutas())
    }

    suspend fun addRuta(ruta: Ruta): Ruta {
        if ( !repositorioRutas.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if ( rutas.contains(ruta) )
            throw RouteException("La ruta ya existe")
        rutas.add(ruta)
        repositorioRutas.addRuta(ruta)
        return ruta
    }

    suspend fun getRutas(): List<Ruta> {
        if ( !repositorioRutas.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        return rutas
    }

    fun builder(): RutaBuilderWrapper {
        return RutaBuilderWrapper(this, calculadorRutas)
    }

}