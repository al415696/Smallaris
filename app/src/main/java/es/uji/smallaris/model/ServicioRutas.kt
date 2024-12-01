package es.uji.smallaris.model

class ServicioRutas(private val calculadorRutas: CalculadorRutas) {

    private val repositorioRutas: RepositorioRutas = RepositorioFirebase()
    private val rutas = mutableListOf<Ruta>()

    fun addRuta(ruta: Ruta): Ruta {
        TODO("Not yet implemented")
    }
    fun getRutas(): List<Ruta> {
        TODO("Not yet implemented")
    }

    fun build(): RutaBuilderWrapper {
        return RutaBuilderWrapper(this, calculadorRutas)
    }

}