package com.palkesz.mr.x.feature.home.authentication.login

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val loginViewModelModule = module {
	viewModelOf(::LoginViewModelImpl)
}
