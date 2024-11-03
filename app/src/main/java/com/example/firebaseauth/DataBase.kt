package com.example.firebaseauth

//import android.annotation.SuppressLint
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore


class DataBase : ComponentActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseAuthTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

fun loadClientes(db: FirebaseFirestore, clientes: MutableList<HashMap<String, String>>) {
    clientes.clear()
    db.collection("Clientes")
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val clienteData = hashMapOf(
                    "id" to document.id,
                    "nome" to (document.getString("nome") ?: ""),
                    "telefone" to (document.getString("telefone") ?: "")
                )
                clientes.add(clienteData)
                Log.d(TAG, "${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Erro ao buscar documentos.", exception)
        }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun App( db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    val clientes = remember { mutableStateListOf<HashMap<String, String>>() }


    loadClientes(db, clientes)

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            Text(text = "App Firebase Firestore")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.3f)
            ) {
                Text(text = "Nome:")
            }
            Column(
            ) {
                TextField(
                    value = nome,
                    onValueChange = { nome = it }
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.3f)
            ) {
                Text(text = "Telefone:")
            }
            Column(
            ) {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it }
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            Button(onClick = {
                val pessoas = hashMapOf(
                    "nome" to nome,
                    "telefone" to telefone
                )

                db.collection("Clientes").add(pessoas)
                    .addOnSuccessListener { documentReference ->
                        loadClientes(db, clientes)
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            })
            {
                Text(text = "Cadastrar")
            }

        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "Nome:")
            }
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "Telefone:")
            }
        }
        Row(
            Modifier
                .fillMaxWidth(),
        ) {
            val clientes = mutableStateListOf<HashMap<String, String>>()
            db.collection("Clientes")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val lista = hashMapOf(
                            "id" to document.id,
                            "nome" to "${document.data.get("nome")}",
                            "telefone" to "${document.data.get("telefone")}",
                        )
                        clientes.add(lista)

                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(clientes) { cliente ->
                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.weight(0.3f)) {
                            Text(text = cliente["nome"] ?: "---")
                        }
                        Column(modifier = Modifier.weight(0.3f)) {
                            Text(text = cliente["telefone"] ?: "---")
                        }
                        Column(modifier = Modifier.weight(0.2f)){
                            Button(onClick = {
                                val clienteId = cliente["id"]

                                if (clienteId != null) {
                                    db.collection("Clientes").document(clienteId).delete()
                                        .addOnSuccessListener {
                                            loadClientes(db, clientes)
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error deleting document", e)
                                        }
                                } else {
                                    Log.w(TAG, "Error: Cliente ID is null")
                                }
                            }) {
                                Text(text = "Deletar")
                            }


                        }

                    }
                }
            }

        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ProjetoComFirebaseTheme {
//        Greeting("Android")
//    }
//}