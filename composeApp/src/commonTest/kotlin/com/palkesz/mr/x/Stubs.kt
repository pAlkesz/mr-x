package com.palkesz.mr.x

import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import kotlinx.coroutines.flow.StateFlow

interface KonnectivityStub : Konnectivity {
    override val isConnectedState: StateFlow<Boolean>
        get() = throw NotImplementedError()
    override val currentNetworkConnection: NetworkConnection
        get() = throw NotImplementedError()
    override val currentNetworkConnectionState: StateFlow<NetworkConnection>
        get() = throw NotImplementedError()
    override val isConnected: Boolean
        get() = throw NotImplementedError()
}
