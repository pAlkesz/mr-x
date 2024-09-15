package com.mr.x.feature.home.authentication.login

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val loginViewModelModule = module {
	factoryOf(::LoginViewModelImpl)
}
