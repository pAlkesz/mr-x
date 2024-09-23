package com.palkesz.mr.x.feature.app

import com.palkesz.mr.x.core.data.dataModule
import com.palkesz.mr.x.feature.games.gameDetailsScreen.gameDetailsModule
import com.palkesz.mr.x.feature.games.myGamesModule
import com.palkesz.mr.x.feature.games.showQrCode.showQrCodeModule
import com.palkesz.mr.x.feature.home.authentication.login.loginModule
import com.palkesz.mr.x.feature.home.createGame.createGameModule
import com.palkesz.mr.x.feature.home.loading.loadingModule
import com.palkesz.mr.x.feature.home.scanQrCode.scanQrCodeModule
import com.palkesz.mr.x.feature.network.networkErrorModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    includes(
        appViewModelModule,
        networkErrorModule,
        createGameModule,
        dataModule,
        gameDetailsModule,
        myGamesModule,
        loginModule,
        loadingModule,
        myGamesModule,
        showQrCodeModule,
        scanQrCodeModule
    )
}

expect val appViewModelModule: Module