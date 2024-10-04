package com.palkesz.mr.x.feature.authentication.login

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    viewModelOf(::LoginViewModelImpl) bind LoginViewModel::class
}
