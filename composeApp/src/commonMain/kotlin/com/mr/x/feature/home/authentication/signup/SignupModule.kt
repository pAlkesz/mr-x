package com.mr.x.feature.home.authentication.signup

import com.mr.x.core.usecase.user.CreateUserUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val signupModule = module {
	factoryOf(::CreateUserUseCase)
	includes(signupViewModelModule)
}

expect val signupViewModelModule: Module
