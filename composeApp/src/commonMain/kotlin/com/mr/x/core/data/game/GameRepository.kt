package com.mr.x.core.data.game

import com.mr.x.core.data.user.AuthRepository
import com.mr.x.core.model.game.Game
import com.mr.x.core.model.game.GameStatus
import com.mr.x.core.util.*
import com.mr.x.core.util.network.NetworkErrorRepository
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface GameRepository {

	val playerGames: StateFlow<List<Game>>
	val gameAnimationEvent: StateFlow<GameAnimationEvent?>

	fun uploadGame(game: Game): Flow<Result<Game>>
	fun updateStatus(gameId: String, status: GameStatus): Flow<Result<Unit>>
	fun getGame(uuid: String): Flow<Result<Game>>
	fun getPlayerGames(playerId: String): Flow<Result<List<Game>>>
	fun joinGameWithGameId(gameId: String, playerId: String): Flow<Result<Unit>>
	suspend fun emitGameEvent(event: GameAnimationEvent?)

}

class GameRepositoryImpl(
	private val firestore: FirebaseFirestore,
	private val authRepository: AuthRepository,
	private val networkErrorRepository: NetworkErrorRepository
) : GameRepository {

	private val _playerGames = MutableStateFlow(emptyList<Game>())
	override val playerGames = _playerGames.asStateFlow()

	private val _gameAnimationEvent = MutableStateFlow<GameAnimationEvent?>(null)
	override val gameAnimationEvent = _gameAnimationEvent.asStateFlow()

	init {
		observePlayerGamesChanges()
	}

	override fun uploadGame(game: Game) = flow<Result<Game>> {
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(game.uuid).set(game)
			emit(Success(game))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override fun updateStatus(gameId: String, status: GameStatus) = flow<Result<Unit>> {
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(gameId).update(
				Pair(STATUS_FIELD, status)
			)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	/** INFO
	 * if a game doesn't exist with the given code catches and emits an error result with
	 * java.lang.IllegalArgumentException: Value was null for non-nullable type com.mr.x.core.model.game.Game
	 * */
	override fun getGame(uuid: String) = flow<Result<Game>> {
		_playerGames.value.find { it.uuid == uuid }?.let {
			emit(Success(it))
			return@flow
		}
		emit(Loading())
		try {
			val res =
				firestore.collection(COLLECTION_NAME).where { UUID_FIELD equalTo uuid }
					.limit(1).get()
			val game = res.documents.map { it.data(Game.serializer()) }.first()
			emit(Success(game))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	override fun getPlayerGames(playerId: String) = flow<Result<List<Game>>> {
		if (_playerGames.value.isNotEmpty()) {
			emit(Success(_playerGames.value))
			return@flow
		}
		emit(Loading())
		try {
			val games =
				playerGamesQuery(playerId).get().documents.map { it.data(Game.serializer()) }
			_playerGames.update { games }
			emit(Success(games))
		}
		catch (e: Exception) {
			emit(Error(e))
		}
	}

	private fun observePlayerGamesChanges() =
		CoroutineHelper.ioScope.launch {
			authRepository.currentUser.flatMapLatest { user ->
				try {
					user?.userId?.let { userId ->
						playerGamesQuery(userId).snapshots.map { snapshot ->
							val (removedGames, addedOrModifiedGames) =
								snapshot.documentChanges.partition { change ->
									change.type == ChangeType.REMOVED
								}
							_playerGames.update { games ->
								(addedOrModifiedGames.map {
									it.document.data(Game.serializer())
								} + games).distinctBy(Game::uuid)
									.subtract(removedGames.map {
										it.document.data(Game.serializer())
									}.toSet()).toList()
							}
						}
					} ?: flowOf()
				}
				catch (e: Exception) {
					e.printStackTrace()
					flowOf()
				}
			}.collect()
		}

	private fun playerGamesQuery(playerId: String) = firestore.collection(COLLECTION_NAME).where {
		any(
			HOST_FIELD equalTo playerId,
			JOINED_PLAYERS_FIELD contains playerId
		)
	}

	override fun joinGameWithGameId(gameId: String, playerId: String) = flow<Result<Unit>> {
		_playerGames.value.find { it.uuid == gameId }?.let {
			emit(Success(Unit))
			return@flow
		}
		emit(Loading())
		try {
			firestore.collection(COLLECTION_NAME).document(gameId).update(
				Pair(JOINED_PLAYERS_FIELD, FieldValue.arrayUnion(playerId))
			)
			emit(Success(Unit))
		}
		catch (e: Exception) {
			networkErrorRepository.addError(e)
			emit(Error(e))
		}
	}

	override suspend fun emitGameEvent(event: GameAnimationEvent?) {
		_gameAnimationEvent.emit(event)
	}


	companion object {
		const val COLLECTION_NAME = "games"
		const val UUID_FIELD = "uuid"
		const val HOST_FIELD = "hostId"
		const val JOINED_PLAYERS_FIELD = "players"
		const val STATUS_FIELD = "status"
	}
}

class GameAnimationEvent(
	val firstName: String,
	val lastName: String
)
