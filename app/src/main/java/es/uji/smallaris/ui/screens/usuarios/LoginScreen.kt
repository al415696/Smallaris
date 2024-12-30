package es.uji.smallaris.ui.screens.usuarios

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uji.smallaris.ui.components.ErrorBubble
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.state.UsuarioViewModel
import java.lang.Error

@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel = viewModel<UsuarioViewModel>(),
    reloadFun: ()-> Unit= {}
) {
    reloadFun()
    LoginScreenContent(
        funIniciarSesion = viewModel::iniciarSesion,
        funRegistrar = viewModel::registrar
    )
}

@Composable
private fun LoginScreenContent(
    funIniciarSesion: suspend (email: String, pass: String)-> String = {_,_ -> ""},
    funRegistrar: suspend (email: String, pass: String)-> String = {_,_ -> ""},
) {
    val loginUser = rememberSaveable { mutableStateOf("") }
    val loginUserValid = rememberSaveable { mutableStateOf(false) }
    val loginPass = rememberSaveable { mutableStateOf("") }
    val loginPassValid = rememberSaveable { mutableStateOf(false) }

    val registerUser = rememberSaveable { mutableStateOf("") }
    val registerUserValid = rememberSaveable { mutableStateOf(false) }
    val registerPass = rememberSaveable { mutableStateOf("") }
    val registerPassValid = rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmailPasswordForm(
                loginUser = loginUser,
                loginUserValid = loginUserValid,
                loginPass = loginPass,
                loginPassValid =loginPassValid,
                textoIntroduccion = "Introduce email y contraseña",
                confirmText = "Iniciar sesión",
                clickAction = funIniciarSesion
            )
            EmailPasswordForm(
                loginUser = registerUser,
                loginUserValid = registerUserValid,
                loginPass = registerPass,
                loginPassValid =registerPassValid,
                textoIntroduccion =
                "¿No tienes cuenta?\nRegístrate con con solo un correo",
                confirmText = "Registrarse",
                clickAction = funRegistrar
            )
        }
    }
}

@Composable
private fun EmailPasswordForm(
    loginUser: MutableState<String>,
    loginUserValid: MutableState<Boolean>,
    loginPass: MutableState<String>,
    loginPassValid: MutableState<Boolean>,
    textoIntroduccion: String = "",
    confirmText: String = "Confirmar",

    clickAction: suspend (email:String, password: String) -> String = {_,_ -> ""}
) {
    val errorText: MutableState<String> = remember{ mutableStateOf("")}
    var confirmado: Boolean by remember { mutableStateOf(false)}

    if (confirmado)
        LaunchedEffect(Unit) {
            errorText.value = clickAction(loginUser.value,loginPass.value)
            confirmado = false
        }
    Surface(
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (textoIntroduccion.isNotEmpty())
                Text(
                    text = textoIntroduccion,
                    textAlign = TextAlign.Center
                )
            FilteredTextField(
                text = loginUser,
                valid = loginUserValid,
                filter = {
                    if (!it.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()))
                        "El correo tiene que ser válido"
                    else
                        ""
                },
                label = "Correo"
            )
            FilteredTextField(
                text = loginPass,
                valid = loginPassValid,
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                enabled = loginUserValid.value && loginPassValid.value,
                onClick = {confirmado = true}
            ) {
                Text(text = confirmText)
            }
            ErrorBubble(errorText = errorText)
        }

    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviewLoginScreen() {
    LoginScreenContent()
}