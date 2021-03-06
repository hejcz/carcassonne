package io.github.hejcz.base

import io.github.hejcz.api.*
import io.github.hejcz.base.tile.*
import io.github.hejcz.engine.Game
import io.github.hejcz.helper.GameScenario
import io.github.hejcz.helper.TestBasicRemainingTiles
import io.github.hejcz.helper.TestGameSetup
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MeepleAvailabilityValidatorSpec : Spek({

    fun playerWithSinglePiece() = Player(id = 1, order = 1, initialPieces = listOf(
        SmallPiece
    ))

    fun playerWithTwoPieces() = Player(id = 1, order = 1, initialPieces = listOf(
        SmallPiece, SmallPiece
    ))

    fun game(player: Player, vararg tiles: BasicTile) =
        Game(setOf(player), TestGameSetup(TestBasicRemainingTiles(*tiles))).dispatch(BeginCmd)

    describe("Game") {

        it("should not allow to put piece player does not have available") {
            GameScenario(game(playerWithSinglePiece(), TileD, TileD))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(2, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Left)))
                .thenReceivedEventShouldBe(NoMeepleEvent(SmallPiece))
        }

        it("should allow to put piece player received from completed object") {
            GameScenario(game(playerWithSinglePiece(), TileD, TileD, TileD))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(1, 1), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(2, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Left)))
                .thenReceivedEventShouldBe(ScoreEvent(1, 3, emptySet()))
        }

        it("should restore all handlers player placed on object") {
            GameScenario(game(playerWithTwoPieces(), TileG, TileV, TileE, TileM, TileU, TileV, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 2), Rotation270))
                .then(PieceCmd(SmallPiece, Knight(Left)))
                .then(TileCmd(Position(0, 2), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(2, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Brigand(Left))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(1, 0), Rotation90))
                .then(PieceCmd(SmallPiece, Peasant(
                    Location(Right)
                )))
                .thenReceivedEventShouldBeOnlyPlaceTile()
        }
    }
})
