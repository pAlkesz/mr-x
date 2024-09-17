package com.palkesz.mr.x.feature.home.authentication.signup

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val signupViewModelModule = module {
	factoryOf(::SignupViewModelImpl)
}
