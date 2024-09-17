package com.palkesz.mr.x.core.model

import com.palkesz.mr.x.core.model.game.Game

data class GameWithHost(
	var game: Game,
	var host: User
)
