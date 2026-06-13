package com.example.estoq.koin

import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import org.koin.dsl.module
import com.example.estoq.data.Viewmodel.User.UserViewModel
import org.koin.core.module.dsl.viewModel

val viewModelModule = module {

    viewModel {
        UserViewModel(get())
    }

    viewModel {
        LoginViewModel(get())
    }
}
