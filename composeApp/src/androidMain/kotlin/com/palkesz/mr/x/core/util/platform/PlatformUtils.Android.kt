package com.palkesz.mr.x.core.util.platform

import androidx.compose.ui.text.intl.Locale
import com.palkesz.mr.x.BuildConfig

actual val isIOS = false

actual val isAndroid = true

actual val isDebug = BuildConfig.DEBUG

actual val locale = Locale.current.language
