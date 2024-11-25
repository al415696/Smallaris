package es.uji.smallaris.model

interface Repositorio {
    suspend fun enFuncionamiento(): Boolean
}