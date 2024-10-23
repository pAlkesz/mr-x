package com.palkesz.mr.x.core.model.game

import com.palkesz.mr.x.core.model.user.User

data class GameWithHost(val game: Game, val host: User)
