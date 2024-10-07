package com.example.base_datos.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ejbasdedatos.Model.User
import com.example.ejbasdedatos.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var users by rememberSaveable { mutableStateOf(listOf<User>()) }
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var isListVisible by rememberSaveable { mutableStateOf(false) }
    var selectedUser by rememberSaveable { mutableStateOf<User?>(null) }
    var userToDelete by rememberSaveable { mutableStateOf<User?>(null) }
    var showDeleteAllConfirmation by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Función para limpiar los campos y selección de usuario
    fun clearInputs() {
        nombre = ""
        apellido = ""
        edad = ""
        selectedUser = null
    }

    // Función para obtener el siguiente ID
    fun getNextId(): Int {
        return if (users.isEmpty()) {
            1 // Si no hay usuarios, el siguiente ID es 1
        } else {
            users.maxOf { it.id } + 1 // De lo contrario, el siguiente ID es el máximo + 1
        }
    }

    // Función para actualizar los IDs de los usuarios
    fun updateUserIds() {
        scope.launch {
            withContext(Dispatchers.IO) {
                val updatedUsers = userRepository.getAllUsers().mapIndexed { index, user ->
                    user.copy(id = index + 1) // Asigna un nuevo ID basado en el índice
                }
                userRepository.deleteAll() // Limpia la base de datos
                updatedUsers.forEach { userRepository.insert(it) } // Inserta los usuarios con IDs actualizados
                users = userRepository.getAllUsers() // Actualiza la lista
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Registro de Usuarios", style = MaterialTheme.typography.titleLarge, color = Color(0xFF6200EE))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text(text = "Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = edad,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    val intAge = it.toIntOrNull()
                    if (intAge == null || (intAge in 0..120)) {
                        edad = it
                    }
                }
            },
            label = { Text(text = "Edad") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (nombre.isNotBlank() && apellido.isNotBlank() && edad.isNotBlank()) {
                    val user = User(
                        id = getNextId(), // Asigna el siguiente ID disponible
                        nombre = nombre,
                        apellido = apellido,
                        edad = edad.toIntOrNull() ?: 0
                    )
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (selectedUser == null) {
                                userRepository.insert(user)
                            } else {
                                userRepository.update(
                                    selectedUser!!.copy(
                                        nombre = nombre,
                                        apellido = apellido,
                                        edad = edad.toIntOrNull() ?: 0
                                    )
                                )
                            }
                        }
                        users = withContext(Dispatchers.IO) {
                            userRepository.getAllUsers()
                        }
                        Toast.makeText(context, if (selectedUser == null) "Usuario Registrado" else "Usuario Modificado", Toast.LENGTH_SHORT).show()
                        clearInputs() // Llama a la función para limpiar los campos
                    }
                } else {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Botón en verde para registrar
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (selectedUser == null) "Registrar" else "Guardar Cambios", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isListVisible = !isListVisible
                if (isListVisible) {
                    scope.launch {
                        users = withContext(Dispatchers.IO) {
                            userRepository.getAllUsers()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), // Botón en azul para listar
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isListVisible) "Ocultar Lista" else "Listar Usuarios", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isListVisible) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text("${user.id}: ${user.nombre} ${user.apellido}, Edad: ${user.edad}")

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = {
                                    nombre = user.nombre
                                    apellido = user.apellido
                                    edad = user.edad.toString()
                                    selectedUser = user // Guardamos el usuario seleccionado para modificar
                                },
                                modifier = Modifier.size(80.dp, 36.dp)
                            ) {
                                Text(text = "Modificar") // Texto completo para modificar
                            }

                            Button(
                                onClick = {
                                    userToDelete = user // Asignamos el usuario a eliminar
                                    showConfirmationDialog = true // Mostramos el cuadro de confirmación
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // Botón en rojo para eliminar
                                modifier = Modifier.size(80.dp, 36.dp)
                            ) {
                                Text(text = "Eliminar") // Texto completo para eliminar
                            }
                        }
                    }
                }
            }
        }

        // Cuadro de diálogo para confirmar eliminación de usuario
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            userToDelete?.let {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        userRepository.deleteById(it.id)
                                        updateUserIds() // Actualiza los IDs después de eliminar
                                    }
                                    Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                    users = userRepository.getAllUsers() // Actualiza la lista de usuarios
                                }
                            }
                            showConfirmationDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Botón en verde para confirmar
                    ) {
                        Text("Sí, eliminar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showConfirmationDialog = false
                            userToDelete = null // Reseteamos la variable al cancelar
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Botón en rojo para cancelar
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text(text = "Confirmación") },
                text = { Text(text = "¿Seguro que deseas eliminar ${userToDelete?.nombre ?: ""} ${userToDelete?.apellido ?: ""}?") }
            )
        }

        // Cuadro de diálogo para confirmar eliminación de todos los usuarios
        if (showDeleteAllConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteAllConfirmation = false },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    userRepository.deleteAll()
                                    users = userRepository.getAllUsers() // Actualiza la lista de usuarios
                                }
                                Toast.makeText(context, "Todos los usuarios eliminados", Toast.LENGTH_SHORT).show()
                            }
                            showDeleteAllConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Botón en verde para eliminar todos
                    ) {
                        Text("Eliminar Todos")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteAllConfirmation = false // Reseteamos la variable al cancelar
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Botón en rojo para cancelar
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text(text = "Confirmación") },
                text = { Text(text = "¿Seguro que deseas eliminar todos los usuarios?") }
            )
        }
    }
}
