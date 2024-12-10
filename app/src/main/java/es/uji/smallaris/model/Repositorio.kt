package es.uji.smallaris.model

import com.google.firebase.firestore.FirebaseFirestore

interface Repositorio {
    suspend fun enFuncionamiento(): Boolean
}