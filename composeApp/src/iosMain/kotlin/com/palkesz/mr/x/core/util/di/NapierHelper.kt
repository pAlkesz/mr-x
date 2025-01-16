package com.palkesz.mr.x.core.util.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Suppress("UNUSED")
fun initNapier() {
    Napier.base(antilog = DebugAntilog())
}
