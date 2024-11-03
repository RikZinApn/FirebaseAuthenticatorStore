package com.example.firebaseauth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.firebaseauth.App
import com.google.firebase.firestore.ktx.firestore


class MainActivity : ComponentActivity() {

    private val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseAuthTheme {
                AppNavigation(auth)
            }
        }
    }
}

@Composable
fun AppNavigation(auth: com.google.firebase.auth.FirebaseAuth) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(auth, navController)
        }
        composable("home") {
            val db = Firebase.firestore
            App(db)
        }
    }
}

@Composable
fun LoginScreen(auth: com.google.firebase.auth.FirebaseAuth, navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Log.e("Auth", "E-mail inválido.")
                            return@Button
                        }

                        if (isRegistering) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("Auth", "Usuário registrado com sucesso!")
                                        email = ""
                                        password = ""
                                        navController.navigate("home")
                                    } else {
                                        Log.e("Auth", "Erro ao registrar: ${task.exception?.message}")
                                    }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("Auth", "Login realizado com sucesso!")
                                        email = ""
                                        password = ""
                                        navController.navigate("home")
                                    } else {
                                        Log.e("Auth", "Erro ao entrar: ${task.exception?.message}")
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isRegistering) "Registrar" else "Entrar")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        isRegistering = !isRegistering
                        email = ""
                        password = ""
                    }
                ) {
                    Text(if (isRegistering) "Já tem conta? Entrar" else "Não tem conta? Registrar")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FirebaseAuthTheme {
        AppNavigation(Firebase.auth)
    }
}
