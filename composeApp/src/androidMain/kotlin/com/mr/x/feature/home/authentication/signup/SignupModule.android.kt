package com.mr.x.feature.home.authentication.signup

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val signupViewModelModule = module {
	viewModelOf(::SignupViewModelImpl)
}
