package io.github.hejcz.base

import io.github.hejcz.api.*
import io.github.hejcz.base.tile.*
import io.github.hejcz.engine.Game
import io.github.hejcz.helper.GameScenario
import io.github.hejcz.helper.Players
import io.github.hejcz.helper.TestBasicRemainingTiles
import io.github.hejcz.helper.TestGameSetup
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object IncompleteRoadEndGameRewardSpec : Spek({

    describe("Incomplete road scoring") {

        fun singlePlayer(vararg tiles: BasicTile) = Game(
            Players.singlePlayer(),
            TestGameSetup(TestBasicRemainingTiles(*tiles))
        ).dispatch(BeginCmd)

        fun multiPlayer(vararg tiles: BasicTile) = Game(
            Players.twoPlayers(),
            TestGameSetup(TestBasicRemainingTiles(*tiles))
        ).dispatch(BeginCmd)

        it("should detect simple incomplete road") {
            GameScenario(singlePlayer(TileK))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Left)))
                .thenReceivedEventShouldBe(ScoreEvent(1, 2, emptySet()))
        }

        it("should not be triggered if there are still some tiles in the deck") {
            GameScenario(singlePlayer(TileK, TileA))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Left))).thenReceivedEventShouldBeOnlyPlaceTile()
        }

        it("should detect simple road made out of 3 tiles") {
            GameScenario(singlePlayer(TileK, TileL))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(1, -1), Rotation90))
                .then(PieceCmd(SmallPiece, Brigand(Down)))
                .thenReceivedEventShouldBe(ScoreEvent(1, 3, emptySet()))
        }

        it("should reward multiple players if they share road with equal number of meeples") {
            GameScenario(multiPlayer(TileK, TileU, TileJ, TileK))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(2, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(1, -1), Rotation270))
                .then(SkipPieceCmd).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(2, -1), Rotation90))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(ScoreEvent(1, 5, emptySet()))
                .thenReceivedEventShouldBe(ScoreEvent(2, 5, emptySet()))
        }

        it("should reward single player if he has advantage of meeples over his opponent") {
            GameScenario(multiPlayer(TileV, TileV, TileB, TileU, TileV, TileV, TileV))
                .then(TileCmd(Position(1, 0), NoRotation)).thenReceivedEventShouldBe(PieceEvent)
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(2, 0), Rotation270)).thenReceivedEventShouldBe(PieceEvent)
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(2, 1), NoRotation)).thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(3, 1), NoRotation)).thenReceivedEventShouldBe(PieceEvent)
                .then(PieceCmd(SmallPiece, Brigand(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(3, 0), Rotation90)).thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(1, -1), Rotation180)).thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd).thenReceivedEventShouldBeOnlyPlaceTile()
                .then(TileCmd(Position(2, -1), Rotation90)).thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(ScoreEvent(2, 7, emptySet()))
                .thenShouldNotReceiveEvent(ScoreEvent(1, 7, emptySet()))
        }
    }
})
