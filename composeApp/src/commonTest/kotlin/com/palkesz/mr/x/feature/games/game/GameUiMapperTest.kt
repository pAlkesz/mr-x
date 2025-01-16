package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.model.question.BarkochbaQuestion
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.model.user.User
import dev.gitlive.firebase.firestore.Timestamp
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import kotlinx.collections.immutable.toImmutableList

@Suppress("Unused")
class GameUiMapperTest : FunSpec({

    test("Mapping view state for game details screen") {
        val gameExhaustive = listOf(
            Game(
                id = TEST_GAME_ID,
                firstName = TEST_FIRST_NAME,
                lastName = TEST_LAST_NAME,
                hostId = TEST_HOST_ID,
                status = GameStatus.ONGOING,
                lastModifiedTimestamp = Timestamp.now(),
            ),
            Game(
                id = TEST_GAME_ID,
                firstName = TEST_FIRST_NAME,
                lastName = TEST_LAST_NAME,
                hostId = TEST_HOST_ID,
                status = GameStatus.FINISHED,
                lastModifiedTimestamp = Timestamp.now(),
            )
        ).exhaustive()
        val host = User(id = TEST_HOST_ID, name = TEST_HOST_NAME)
        val playerAnswerArb = arbitrary {
            Answer(
                firstName = listOf(
                    TEST_EXPECTED_FIRST_NAME,
                    TEST_NOT_EXPECTED_FIRST_NAME
                ).exhaustive().toArb().bind(),
                lastName = listOf(
                    TEST_EXPECTED_LAST_NAME,
                    TEST_NOT_EXPECTED_LAST_NAME
                ).exhaustive().toArb().bind(),
                userId = TEST_PLAYER_TWO_ID,
            )
        }
        val hostAnswerArb = arbitrary {
            Answer(
                firstName = listOf(
                    TEST_EXPECTED_FIRST_NAME,
                    TEST_NOT_EXPECTED_FIRST_NAME
                ).exhaustive().toArb().bind(),
                lastName = listOf(
                    TEST_EXPECTED_LAST_NAME,
                    TEST_NOT_EXPECTED_LAST_NAME
                ).exhaustive().toArb().bind(),
                userId = TEST_HOST_ID,
            )
        }
        val players = listOf(
            User(id = TEST_USER_ID, name = TEST_USER_NAME),
            User(id = TEST_PLAYER_ID, name = TEST_PLAYER_NAME),
            User(id = TEST_PLAYER_TWO_ID, name = TEST_PLAYER_TWO_NAME)
        )
        val questionArb = arbitrary {
            Question(
                id = Arb.string().bind(),
                userId = listOf(TEST_USER_ID, TEST_PLAYER_ID).exhaustive().toArb().bind(),
                gameId = TEST_GAME_ID,
                number = Arb.int().bind(),
                expectedFirstName = TEST_EXPECTED_FIRST_NAME,
                expectedLastName = TEST_EXPECTED_LAST_NAME,
                status = QuestionStatus.entries.exhaustive().toArb().bind(),
                playerAnswer = playerAnswerArb.bind(),
                hostAnswer = hostAnswerArb.bind(),
                text = Arb.string().bind(),
                lastModifiedTimestamp = Timestamp.now(),
            )
        }
        val barkochbaArb = arbitrary {
            BarkochbaQuestion(
                id = Arb.string().bind(),
                gameId = TEST_GAME_ID,
                userId = listOf(TEST_USER_ID, TEST_PLAYER_ID).exhaustive().toArb().bind(),
                status = BarkochbaStatus.entries.exhaustive().toArb().bind(),
                number = Arb.int().bind(),
                text = Arb.string().bind(),
                answer = Arb.boolean().bind(),
                lastModifiedTimestamp = Timestamp.now(),
            )
        }
        val resultArb = arbitrary {
            GameResult(
                game = gameExhaustive.toArb().bind(),
                host = host,
                questions = questionArb.take(count = 50).toList().toImmutableList(),
                barkochbaQuestions = barkochbaArb.take(count = 50).toList().toImmutableList(),
                players = players,
            )
        }
        val eventExhaustive =
            listOf(GameEvent.NavigateToQrCode(gameId = TEST_GAME_ID), null).exhaustive()
        val authRepository = object : AuthRepository.Stub {
            override val userId = TEST_USER_ID
        }
        val mapper = GameUiMapperImpl(authRepository = authRepository)
        checkAll(resultArb, eventExhaustive) { result, event ->
            mapper.mapViewState(
                result = result,
                notificationCount = 0 to 0,
                addedQuestionId = null,
                addedBarkochbaQuestionId = null,
                event = event
            ).apply {
                this.host shouldBe result.host.name.takeIf { !isHost }
                this.event shouldBe event
                firstName shouldBe result.game.firstName
                lastName shouldBe result.game.lastName
                barkochbaQuestionCount shouldBe result.barkochbaQuestions.count { it.status == BarkochbaStatus.IN_STORE }
                isGameOngoing shouldBe (result.game.status == GameStatus.ONGOING)
                isAskQuestionButtonVisible shouldBe (result.host.id != authRepository.userId && result.game.status == GameStatus.ONGOING)
                questions.size shouldBeEqual result.questions.size
                barkochbaQuestions.size shouldBeEqual result.barkochbaQuestions.count { it.status != BarkochbaStatus.IN_STORE }
            }
        }
    }
})

private const val TEST_HOST_ID = "TEST_HOST_ID"
private const val TEST_HOST_NAME = "TEST_HOST_NAME"
private const val TEST_FIRST_NAME = "TEST_FIRST_NAME"
private const val TEST_LAST_NAME = "TEST_LAST_NAME"
private const val TEST_GAME_ID = "TEST_GAME_ID"
private const val TEST_USER_ID = "TEST_USER_ID"
private const val TEST_USER_NAME = "TEST_USER_NAME"
private const val TEST_PLAYER_ID = "TEST_PLAYER_ID"
private const val TEST_PLAYER_NAME = "TEST_PLAYER_NAME"
private const val TEST_PLAYER_TWO_ID = "TEST_PLAYER_TWO_ID"
private const val TEST_PLAYER_TWO_NAME = "TEST_PLAYER_TWO_NAME"
private const val TEST_EXPECTED_FIRST_NAME = "TEST_EXPECTED_FIRST_NAME"
private const val TEST_EXPECTED_LAST_NAME = "TEST_EXPECTED_LAST_NAME"
private const val TEST_NOT_EXPECTED_FIRST_NAME = "TEST_NOT_EXPECTED_FIRST_NAME"
private const val TEST_NOT_EXPECTED_LAST_NAME = "TEST_NOT_EXPECTED_LAST_NAME"
