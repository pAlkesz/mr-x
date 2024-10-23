package com.palkesz.mr.x.feature.home

import com.palkesz.mr.x.feature.home.create.createGameModule
import com.palkesz.mr.x.feature.home.join.joinGameModule
import org.koin.dsl.module

val homeModule = module {
    includes(createGameModule, joinGameModule)
}
