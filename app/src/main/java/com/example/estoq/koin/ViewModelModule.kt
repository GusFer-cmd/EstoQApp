package com.example.estoq.koin

import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import com.example.estoq.data.Viewmodel.Client.ClientViewModel
import com.example.estoq.data.Viewmodel.Item.ItemViewModel
import org.koin.dsl.module
import com.example.estoq.data.Viewmodel.Storage.StorageViewModel
import com.example.estoq.data.Viewmodel.User.UserViewModel
import org.koin.core.module.dsl.viewModel

val viewModelModule = module {

    viewModel {
        UserViewModel(get())
    }

    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        StorageViewModel(get())
    }

    viewModel {
        ItemViewModel(get(), get())
    }

    viewModel {
        ClientViewModel(get())
    }
}
