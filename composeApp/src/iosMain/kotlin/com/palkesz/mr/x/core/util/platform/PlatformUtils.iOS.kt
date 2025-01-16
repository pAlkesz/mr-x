package com.palkesz.mr.x.core.util.platform

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual val isIOS = true

actual val isAndroid = false

actual val isDebug = Platform.isDebugBinary

actual val locale = NSLocale.currentLocale.languageCode
