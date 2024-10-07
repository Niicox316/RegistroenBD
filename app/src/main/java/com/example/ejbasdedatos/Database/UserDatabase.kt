package com.example.ejbasdedatos.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.ejbasdedatos.Model.User

@Database(entities = [User::class], version = 1) // Asegúrate de incluir las entidades aquí
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao // Método para acceder a UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database" // Nombre de la base de datos
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
