package com.palkesz.mr.x.feature.authentication

import com.palkesz.mr.x.core.usecase.auth.IsUsernameUploadedUseCase
import com.palkesz.mr.x.core.usecase.auth.SendSignInLinkUseCase
import com.palkesz.mr.x.core.usecase.auth.SignInWithLinkUseCase
import com.palkesz.mr.x.core.usecase.auth.UpdateUsernameUseCase
import com.palkesz.mr.x.feature.authentication.login.loginModule
import com.palkesz.mr.x.feature.authentication.username.addUsernameModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authModule = module {
    includes(loginModule, addUsernameModule)
    factoryOf(::SignInWithLinkUseCase)
    factoryOf(::SendSignInLinkUseCase)
    factoryOf(::UpdateUsernameUseCase)
    factoryOf(::IsUsernameUploadedUseCase)
}
