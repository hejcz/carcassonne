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

object CompletedCastleDetectionSpec : Spek({

    describe("A castle detector") {

        fun singlePlayer(vararg tiles: BasicTile) = Game(
            Players.singlePlayer(),
            TestGameSetup(TestBasicRemainingTiles(*tiles))
        ).dispatch(BeginCmd)

        fun multiPlayer(vararg tiles: BasicTile) = Game(
            Players.twoPlayers(),
            TestGameSetup(TestBasicRemainingTiles(*tiles))
        ).dispatch(BeginCmd)

        it("should not detect incomplete castle with knight") {
            GameScenario(singlePlayer(TileD, TileB))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .thenReceivedEventShouldBeOnlyPlaceTile()
        }

        it("should not detect incomplete castle without knight") {
            GameScenario(singlePlayer(TileD, TileB))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBeOnlyPlaceTile()
        }

        it("should detect XS completed castle") {
            GameScenario(singlePlayer(TileD))
                .then(TileCmd(Position(0, 1), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 4, setOf(PieceOnBoard(
                        Position(0, 1),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should detect S completed castle") {
            GameScenario(singlePlayer(TileG, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 6, setOf(PieceOnBoard(
                        Position(0, 2),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should not detect S incomplete castle") {
            GameScenario(singlePlayer(TileR, TileD, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down))).thenReceivedEventShouldBeOnlyPlaceTile()
        }

        it("should detect M incomplete castle") {
            GameScenario(singlePlayer(TileR, TileD, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(PieceCmd(SmallPiece, Knight(Left)))
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 8, setOf(PieceOnBoard(
                        Position(1, 1),
                        SmallPiece, Knight(Left))))
                )
        }

        it("should detect meeples placed before castle completion") {
            GameScenario(singlePlayer(TileR, TileD, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 8, setOf(PieceOnBoard(
                        Position(0, 1),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should return all meeples placed on completed castle") {
            GameScenario(singlePlayer(TileR, TileD, TileD, TileN))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 2), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(
                        1, 10, setOf(
                            PieceOnBoard(
                                Position(0, 1), SmallPiece, Knight(
                                    Down
                                )),
                            PieceOnBoard(
                                Position(1, 2), SmallPiece, Knight(
                                    Down
                                ))
                        )
                    )
                )
        }

        it("should not give any reward if no handlers were on completed castle") {
            GameScenario(singlePlayer(TileR, TileD, TileD, TileN, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 1), Rotation270)).thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd).thenReceivedEventShouldBeOnlyPlaceTile()
        }

        it("should modify score if emblems are available on castle") {
            GameScenario(singlePlayer(TileF, TileM, TileD))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(0, 2), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 2), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 12, setOf(PieceOnBoard(
                        Position(0, 1),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should reward many players if they have the same amount of handlers in a castle") {
            GameScenario(multiPlayer(TileR, TileD, TileA, TileD, TileN))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 0), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 2), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 10, setOf(PieceOnBoard(
                        Position(0, 1),
                        SmallPiece, Knight(Down))))
                )
                .thenReceivedEventShouldBe(
                    ScoreEvent(2, 10, setOf(PieceOnBoard(
                        Position(1, 2),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should reward only one player if he has advantage of handlers in castle") {
            GameScenario(multiPlayer(TileR, TileD, TileD, TileD, TileR))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(0, 2), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(1, 2), Rotation180))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(
                        1, 12, setOf(
                            PieceOnBoard(
                                Position(0, 1), SmallPiece, Knight(
                                    Down
                                )),
                            PieceOnBoard(
                                Position(1, 0), SmallPiece, Knight(
                                    Up
                                ))
                        )
                    )
                )
                .thenReceivedEventShouldBe(
                    NoScoreEvent(2, setOf(PieceOnBoard(
                        Position(1, 2),
                        SmallPiece, Knight(Down))))
                )
        }

        it("should detect that multiple castles were finished with single tile") {
            GameScenario(multiPlayer(TileN, TileD, TileI))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(PieceCmd(SmallPiece, Knight(Down)))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(1, 1), Rotation270))
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 6, setOf(PieceOnBoard(
                        Position(0, 1),
                        SmallPiece, Knight(Down))))
                )
                .thenReceivedEventShouldBe(
                    ScoreEvent(2, 4, setOf(PieceOnBoard(
                        Position(1, 0),
                        SmallPiece, Knight(Up))))
                )
        }

        it("should return single score event if the same castle is on multiple tiles directions") {
            GameScenario(singlePlayer(TileD, TileN, TileN))
                .then(TileCmd(Position(1, 0), NoRotation))
                .then(PieceCmd(SmallPiece, Knight(Up)))
                .then(TileCmd(Position(0, 1), Rotation90))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(1, 1), Rotation180))
                .thenReceivedEventShouldBe(PieceEvent)
                .then(SkipPieceCmd)
                .thenReceivedEventShouldBe(
                    ScoreEvent(1, 8, setOf(PieceOnBoard(
                        Position(1, 0),
                        SmallPiece, Knight(Up))))
                )
        }
    }
})
