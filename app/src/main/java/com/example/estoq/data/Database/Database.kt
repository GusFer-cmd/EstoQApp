package com.example.estoq.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.estoq.data.Dao.Item.ItemDao
import com.example.estoq.data.Dao.Storage.StorageDao
import com.example.estoq.data.Dao.User.UserDao
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.Storage.Storage
import com.example.estoq.data.Model.User.User

@Database(
    entities = [User::class, Storage::class, Item::class],
    version = 4
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DB_NAME = "estoq_db"
    }

    abstract fun userDao() : UserDao
    abstract fun storageDao() : StorageDao
    abstract fun itemDao() : ItemDao
}