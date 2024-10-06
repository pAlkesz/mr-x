package com.palkesz.mr.x.feature.app

import com.palkesz.mr.x.core.data.dataModule
import com.palkesz.mr.x.feature.authentication.authModule
import com.palkesz.mr.x.feature.games.gameDetailsScreen.gameDetailsModule
import com.palkesz.mr.x.feature.games.myGamesModule
import com.palkesz.mr.x.feature.games.showQrCode.showQrCodeModule
import com.palkesz.mr.x.feature.home.createGame.createGameModule
import com.palkesz.mr.x.feature.home.scanQrCode.scanQrCodeModule
import com.palkesz.mr.x.feature.network.networkErrorModule
import com.plusmobileapps.konnectivity.Konnectivity
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModelImpl) bind AppViewModel::class
    single { Konnectivity() }
    includes(
        networkErrorModule,
        createGameModule,
        dataModule,
        gameDetailsModule,
        myGamesModule,
        myGamesModule,
        showQrCodeModule,
        scanQrCodeModule,
        authModule,
    )
}
