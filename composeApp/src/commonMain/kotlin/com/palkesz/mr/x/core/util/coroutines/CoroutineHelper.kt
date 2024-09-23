package com.palkesz.mr.x.core.util.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object CoroutineHelper {
    val ioScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    val mainScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }
}
