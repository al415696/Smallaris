package es.uji.smallaris.model

class UnloggedUserException(message: String = "No hay una sesión activa/iniciada.") : Exception(message)