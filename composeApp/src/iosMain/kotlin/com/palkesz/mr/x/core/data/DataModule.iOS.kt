package com.palkesz.mr.x.core.data

import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val dataStoreModule = module {
    single {
        createDataStore(producePath = {
            getPath(fileName = DATA_STORE_FILE_NAME).toPath()
        })
    }
}

private fun getPath(fileName: String): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path + "/$fileName"
}
