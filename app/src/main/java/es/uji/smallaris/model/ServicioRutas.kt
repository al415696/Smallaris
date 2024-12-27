package es.uji.smallaris.model

import androidx.collection.emptyLongSet
import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

class ServicioRutas(
    private val calculadorRutas: CalculadorRutas,
    private val repositorioRutas: RepositorioRutas = RepositorioFirebase(),
    private val servicioRutasYCoste: ServicioAPIs = ServicioAPIs
) {
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
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        if (rutas.contains(ruta))
            throw RouteException("La ruta ya existe")
        if  (repositorioRutas.addRuta(ruta))
            rutas.add(ruta)
        else
            throw RouteException("No se pudo añadir la ruta por un problema remoto")
        return ruta
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getRutas(ordenRuta: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE): List<Ruta> {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        return rutas.sortedWith(
            ordenRuta.comparator()
        )
    }

    fun builder(): RutaBuilderWrapper {
        return RutaBuilderWrapper(calculadorRutas, servicioRutasYCoste, this)
    }

    @Throws(ConnectionErrorException::class)
    suspend fun setFavorito(ruta: Ruta, favorito: Boolean = true): Boolean {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        if (ruta.isFavorito() == favorito)
            return false
        ruta.setFavorito(favorito)
        if (rutas.contains(ruta)) {
            repositorioRutas.setRutaFavorita(ruta, favorito)
            return true
        }
        return false
    }

    @Throws(ConnectionErrorException::class, RouteException::class)
    suspend fun deleteRuta(ruta: Ruta): Boolean {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")

        if (ruta.isFavorito()) {
            throw RouteException("Ruta favorita")
        }

        rutas.remove(ruta)
        return repositorioRutas.deleteRuta(ruta)
    }

    fun contains(ruta: Ruta): Boolean {
        return rutas.contains(ruta)
    }

    companion object{
        private lateinit var servicio: ServicioRutas
        fun getInstance(): ServicioRutas{
            if (!this::servicio.isInitialized){
                servicio = ServicioRutas(CalculadorRutasORS(ServicioAPIs), RepositorioFirebase.getInstance(), ServicioAPIs)
            }
            return servicio
        }
    }

}