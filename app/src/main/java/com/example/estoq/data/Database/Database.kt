package com.example.estoq.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.estoq.data.Dao.User.UserDao
import com.example.estoq.data.Model.User.User

@Database(
    entities = [User::class],
    version = 1
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DB_NAME = "estoq_db"
    }

    abstract fun userDao() : UserDao
}