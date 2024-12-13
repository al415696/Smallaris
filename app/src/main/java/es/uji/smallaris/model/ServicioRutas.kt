package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

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

    @Throws(ConnectionErrorException::class, RouteException::class)
    suspend fun addRuta(ruta: Ruta): Ruta {
        if ( !repositorioRutas.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if ( rutas.contains(ruta) )
            throw RouteException("La ruta ya existe")
        rutas.add(ruta)
        repositorioRutas.addRuta(ruta)
        return ruta
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getRutas(ordenRuta: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE): List<Ruta> {
        if ( !repositorioRutas.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        return rutas.sortedWith(
            ordenRuta.comparator()
        )
    }

    fun builder(): RutaBuilderWrapper {
        return RutaBuilderWrapper(this, calculadorRutas)
    }

    @Throws(ConnectionErrorException::class)
    suspend fun setFavorito(ruta: Ruta, favorito: Boolean = true): Boolean{
        if ( !repositorioRutas.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (ruta.isFavorito() == favorito)
            return false
        ruta.setFavorito(favorito)
        if (rutas.contains(ruta)){
            repositorioRutas.setRutaFavorita(ruta, favorito)
            return true
        }
        return false
    }

    suspend fun deleteRuta(ruta: Ruta): Boolean {
       return false
    }

}