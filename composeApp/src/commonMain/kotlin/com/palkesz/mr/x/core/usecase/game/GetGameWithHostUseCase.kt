package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.GameWithHost
import com.palkesz.mr.x.core.model.User
import com.palkesz.mr.x.core.util.extensions.mapResult

class GetGameWithHostUseCase(
    private val userRepository: UserRepository,
    private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) {

    fun run(gameId: String) = getAndObserveGameUseCase.run(gameId).mapResult { game ->
        // FIXME
        //userRepository.fetchUser(game.hostId).mapResult {
        GameWithHost(game = game, host = User(id = "", name = ""))
        //}
    }
}
