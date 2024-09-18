package com.mr.x.di

import com.mr.x.core.data.dataModule
import com.mr.x.core.util.network.networkErrorModule
import com.mr.x.feature.games.gameDetailsScreen.gameDetailsModule
import com.mr.x.feature.games.myGamesModule
import com.mr.x.feature.games.showQrCode.showQrCodeModule
import com.mr.x.feature.home.authentication.login.loginModule
import com.mr.x.feature.home.createGame.createGameModule
import com.mr.x.feature.home.loading.loadingModule
import com.mr.x.feature.home.scanQrCode.scanQrCodeModule
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule() = module {
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
