package com.palkesz.mr.x.feature.games.qrcode

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val qrCodeModule = module {
    includes(shareSheetHelperModule)
    viewModel { parameters ->
        QrCodeViewModelImpl(parameters.get(), get())
    } bind QrCodeViewModel::class
}

expect val shareSheetHelperModule: Module
