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
        ).build()
    }

    single {
        get<Database>().userDao()
    }
}