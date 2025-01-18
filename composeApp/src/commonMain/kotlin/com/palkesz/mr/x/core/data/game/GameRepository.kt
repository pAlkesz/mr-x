package com.palkesz.mr.x.core.data.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.util.extensions.map
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromDuration
import dev.gitlive.firebase.firestore.toDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.days

interface GameRepository {
    val games: StateFlow<List<Game>>

    suspend fun createGame(game: Game): Result<Game>
    suspend fun updateStatus(id: String, status: GameStatus): Result<Unit>
    suspend fun fetchGame(id: String): Result<Game>
    suspend fun fetchGames(playerId: String): Result<List<Game>>
    suspend fun joinGame(gameId: String, playerId: String): Result<Unit>
    suspend fun observeGames()

    interface Stub : GameRepository {
        override val games: StateFlow<List<Game>>
            get() = throw NotImplementedError()

        override suspend fun createGame(game: Game): Result<Game> = throw NotImplementedError()
        override suspend fun updateStatus(id: String, status: GameStatus): Result<Unit> =
            throw NotImplementedError()

        override suspend fun fetchGame(id: String): Result<Game> = throw NotImplementedError()
        override suspend fun fetchGames(playerId: String): Result<List<Game>> =
            throw NotImplementedError()

        override suspend fun joinGame(gameId: String, playerId: String): Result<Unit> =
            throw NotImplementedError()

        override suspend fun observeGames(): Unit = throw NotImplementedError()
    }
}

class GameRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : GameRepository {

    private val _games = MutableStateFlow(emptyList<Game>())
    override val games = _games.asStateFlow()

    override suspend fun createGame(game: Game) = runCatching {
        firestore.collection(GAMES_COLLECTION_KEY).document(game.id).set(game)
        game
    }

    override suspend fun updateStatus(id: String, status: GameStatus) = runCatching {
        firestore.collection(GAMES_COLLECTION_KEY)
            .document(id)
            .update(Pair(STATUS_FIELD_KEY, status))
    }

    override suspend fun fetchGame(id: String) = runCatching {
        games.value.find { it.id == id }?.let { game ->
            return@runCatching game
        }
        firestore.collection(GAMES_COLLECTION_KEY).where {
            ID_FIELD_KEY equalTo id
        }.limit(1).get().documents.map { it.data(Game.serializer()) }.first().also { game ->
            _games.update { cachedGames ->
                (listOf(game) + cachedGames).distinctBy { it.id }
                    .sortedByDescending { it.lastModifiedTimestamp.seconds }
            }
        }
    }

    override suspend fun fetchGames(playerId: String) = runCatching {
        if (games.value.isNotEmpty()) {
            return@runCatching games.value
        }
        getGamesQuery(playerId).get().documents.map { it.data(Game.serializer()) }.also { games ->
            _games.update { games }
        }
    }

    override suspend fun joinGame(gameId: String, playerId: String) = runCatching {
        _games.value.find { it.id == gameId }?.let {
            return@runCatching
        }
        firestore.collection(GAMES_COLLECTION_KEY).document(gameId).update(
            Pair(PLAYERS_FIELD_KEY, FieldValue.arrayUnion(playerId))
        )
    }

    override suspend fun observeGames() {
        authRepository.userId?.let { userId ->
            getGamesQuery(userId).snapshots.map { snapshot ->
                snapshot.documentChanges.partition { change ->
                    change.type == ChangeType.REMOVED
                }.map { it.document.data(Game.serializer()) }
            }.collect { (removedGames, addedOrModifiedGames) ->
                _games.update { games ->
                    (addedOrModifiedGames + games).distinctBy { game -> game.id }
                        .subtract(removedGames.toSet())
                        .sortedByDescending { it.lastModifiedTimestamp.seconds }.toList()
                }
            }
        }
    }

    private fun getGamesQuery(playerId: String) = firestore.collection(GAMES_COLLECTION_KEY).where {
        any(HOST_FIELD_KEY equalTo playerId, PLAYERS_FIELD_KEY contains playerId)?.and(
            LAST_MODIFIED_FIELD_KEY greaterThan Timestamp.fromDuration(
                duration = Timestamp.now().toDuration() - 30.days
            )
        )
    }.orderBy(LAST_MODIFIED_FIELD_KEY, direction = Direction.DESCENDING).limit(GAMES_FETCH_LIMIT)

    companion object {
        private const val GAMES_COLLECTION_KEY = "games"
        private const val ID_FIELD_KEY = "id"
        private const val HOST_FIELD_KEY = "hostId"
        private const val PLAYERS_FIELD_KEY = "players"
        private const val STATUS_FIELD_KEY = "status"
        private const val LAST_MODIFIED_FIELD_KEY = "lastModifiedTimestamp"
        private const val GAMES_FETCH_LIMIT = 50
    }
}
