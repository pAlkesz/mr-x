package com.palkesz.mr.x.core.data.datastore.notifications

import androidx.datastore.core.okio.OkioSerializer
import com.palkesz.mr.x.proto.LocalNotificationMap
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

object LocalNotificationSerializer : OkioSerializer<LocalNotificationMap> {

    override val defaultValue: LocalNotificationMap
        get() = LocalNotificationMap()

    override suspend fun readFrom(source: BufferedSource): LocalNotificationMap = try {
        LocalNotificationMap.ADAPTER.decode(source)
    } catch (exception: IOException) {
        throw Exception(exception.message)
    }

    override suspend fun writeTo(t: LocalNotificationMap, sink: BufferedSink) {
        sink.write(t.encode())
    }
}
