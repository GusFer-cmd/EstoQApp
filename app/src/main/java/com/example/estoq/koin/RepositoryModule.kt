package com.example.estoq.koin

import com.example.estoq.data.Repository.User.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        UserRepository(get())
    }
}