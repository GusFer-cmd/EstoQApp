package com.example.estoq.koin

import com.example.estoq.data.Repository.Client.ClientRepository
import com.example.estoq.data.Repository.Item.ItemRepository
import com.example.estoq.data.Repository.Network.AddressRepository
import com.example.estoq.data.Repository.PivotSaleItem.PivotSaleItemRepository
import com.example.estoq.data.Repository.SaleArchive.SaleArchiveRepository
import com.example.estoq.data.Repository.Storage.StorageRepository
import com.example.estoq.data.Repository.User.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        UserRepository(get())
    }

    single {
        StorageRepository(get())
    }

    single {
        ItemRepository(get())
    }

    single {
        ClientRepository(get())
    }

    single {
        SaleArchiveRepository(get())
    }

    single {
        PivotSaleItemRepository(get())
    }

    single {
        AddressRepository(get())
    }
}