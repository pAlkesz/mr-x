package com.palkesz.mr.x.feature.app

import kotlinx.serialization.Serializable

sealed interface MrXGraph {

    @Serializable
    data object Auth : MrXGraph

    @Serializable
    data object Home : MrXGraph

    @Serializable
    data object Games : MrXGraph

}
