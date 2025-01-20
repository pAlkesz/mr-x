package com.palkesz.mr.x.core.data.datastore

import androidx.datastore.core.okio.OkioSerializer
import com.palkesz.mr.x.proto.MrXData
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

object MrXDataSerializer : OkioSerializer<MrXData> {

    override val defaultValue: MrXData
        get() = MrXData()

    override suspend fun readFrom(source: BufferedSource): MrXData = try {
        MrXData.ADAPTER.decode(source)
    } catch (exception: IOException) {
        throw Exception(exception.message)
    }

    override suspend fun writeTo(t: MrXData, sink: BufferedSink) {
        sink.write(t.encode())
    }
}
