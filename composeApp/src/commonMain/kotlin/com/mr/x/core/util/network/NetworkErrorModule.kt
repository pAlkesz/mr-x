package com.mr.x.core.util.network

import org.koin.dsl.module

val networkErrorModule = module {
	single { NetworkErrorRepository() }
}