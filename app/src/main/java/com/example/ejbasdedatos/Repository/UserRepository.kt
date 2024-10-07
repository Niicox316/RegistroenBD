package com.example.ejbasdedatos.Repository

import com.example.ejbasdedatos.Model.User
import com.example.ejbasdedatos.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class UserRepository(private val userDatabase: UserDatabase) {

    // Método para insertar un usuario
    suspend fun insert(user: User) {
        withContext(Dispatchers.IO) {
            userDatabase.userDao().insert(user)
        }
    }

    // Método para actualizar un usuario
    suspend fun update(user: User) {
        withContext(Dispatchers.IO) {
            userDatabase.userDao().update(user)
        }
    }

    // Método para eliminar un usuario
    suspend fun delete(user: User) {
        withContext(Dispatchers.IO) {
            userDatabase.userDao().delete(user)
        }
    }

    // Método para obtener todos los usuarios
    suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            userDatabase.userDao().getAllUsers()
        }
    }

    // Método para eliminar todos los usuarios
    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            userDatabase.userDao().deleteAll()
        }
    }

    // Método para eliminar un usuario por ID
    suspend fun deleteById(userId: Int) {
        withContext(Dispatchers.IO) {
            userDatabase.userDao().deleteById(userId)
        }
    }
}
