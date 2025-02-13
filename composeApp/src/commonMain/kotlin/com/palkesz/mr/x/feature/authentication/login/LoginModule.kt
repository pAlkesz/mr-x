package com.palkesz.mr.x.feature.authentication.login

import com.palkesz.mr.x.core.usecase.auth.SignInWithPasswordUseCase
import com.palkesz.mr.x.core.usecase.auth.SignInWithPasswordUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    factoryOf(::SignInWithPasswordUseCaseImpl) bind SignInWithPasswordUseCase::class
    viewModelOf(::LoginViewModelImpl) bind LoginViewModel::class
}
