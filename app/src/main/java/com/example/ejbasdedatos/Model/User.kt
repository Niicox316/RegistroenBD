package com.example.ejbasdedatos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // Nombre de la tabla en la base de datos
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID del usuario, autogenerado
    val nombre: String,  // Nombre del usuario
    val apellido: String, // Apellido del usuario
    val edad: Int       // Edad del usuario
)
