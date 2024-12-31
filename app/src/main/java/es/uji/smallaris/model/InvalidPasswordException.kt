package es.uji.smallaris.model

class InvalidPasswordException(message: String = "La contraseña es inválida, ya que debe tener al menos 8 carácteres.") : Exception(message)