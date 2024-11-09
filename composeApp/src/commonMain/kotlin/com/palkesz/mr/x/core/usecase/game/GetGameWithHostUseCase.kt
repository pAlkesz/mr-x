package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.user.UserRepository

class GetGameWithHostUseCase(
    private val userRepository: UserRepository,
    private val getAndObserveGameUseCase: FetchGameResultUseCase
) {

    /*fun run(gameId: String) = getAndObserveGameUseCase.run(gameId).mapResult { game ->
        // FIXME
        //userRepository.fetchUser(game.hostId).mapResult {
        GameWithHost(game = game, host = User(id = "", name = ""))
        //}
    }*/
}
