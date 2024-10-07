package com.example.ejbasdedatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.base_datos.Screen.UserApp
import com.example.ejbasdedatos.Repository.UserRepository
import com.example.ejbasdedatos.database.UserDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDatabase = UserDatabase.getDatabase(applicationContext) // Obtener instancia de la base de datos
        val userRepository = UserRepository(userDatabase) // Crear el repositorio

        setContent {
            UserApp(userRepository = userRepository) // No necesitas el parámetro onBackToMenu
        }
    }

}
