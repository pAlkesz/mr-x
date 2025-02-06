package com.palkesz.mr.x.feature.home.settings

import com.palkesz.mr.x.core.usecase.auth.DeleteAccountUseCase
import com.palkesz.mr.x.core.usecase.auth.DeleteAccountUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    factoryOf(::DeleteAccountUseCaseImpl) bind DeleteAccountUseCase::class
    viewModelOf(::SettingsViewModelImpl) bind SettingsViewModel::class
}
