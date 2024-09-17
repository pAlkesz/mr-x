package com.palkesz.mr.x.feature.home.authentication.login

import com.palkesz.mr.x.feature.home.authentication.signup.signupModule
import org.koin.core.module.Module
import org.koin.dsl.module

val loginModule = module {
	includes(
		loginViewModelModule,
		signupModule
	)
}

expect val loginViewModelModule: Module
