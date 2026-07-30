package com.example.estoq.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.estoq.data.Dao.Client.ClientDao
import com.example.estoq.data.Dao.Item.ItemDao
import com.example.estoq.data.Dao.PivotSaleItem.PivotSaleItemDao
import com.example.estoq.data.Dao.SaleArchive.SaleArchiveDao
import com.example.estoq.data.Dao.Storage.StorageDao
import com.example.estoq.data.Dao.User.UserDao
import com.example.estoq.data.Model.Client.Client
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem
import com.example.estoq.data.Model.SaleArchive.SaleArchive
import com.example.estoq.data.Model.Storage.Storage
import com.example.estoq.data.Model.User.User

@Database(
    entities = [User::class, Storage::class, Item::class, Client::class, SaleArchive::class, PivotSaleItem::class],
    version = 11
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    companion object {
        const val DB_NAME = "estoq_db"
    }

    abstract fun userDao() : UserDao
    abstract fun storageDao() : StorageDao
    abstract fun itemDao() : ItemDao
    abstract fun clientDao() : ClientDao
    abstract fun saleArchiveDao() : SaleArchiveDao
    abstract fun pivotSaleItemDao() : PivotSaleItemDao
}