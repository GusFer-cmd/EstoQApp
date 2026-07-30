package com.example.estoq.koin

import androidx.room.Room
import com.example.estoq.data.Database.Database
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            Database::class.java,
            "estoq_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    single {
        get<Database>().userDao()
    }

    single {
        get<Database>().storageDao()
    }

    single {
        get<Database>().itemDao()
    }

    single {
        get<Database>().clientDao()
    }

    single {
        get<Database>().saleArchiveDao()
    }

    single {
        get<Database>().pivotSaleItemDao()
    }

}