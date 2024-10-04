package com.palkesz.mr.x.feature.authentication.username

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addUsernameModule = module {
    viewModelOf(::AddUsernameViewModelImpl) bind AddUsernameViewModel::class
}
