package io.github.hejcz.base

import io.github.hejcz.api.*
import io.github.hejcz.base.tile.TileA
import io.github.hejcz.base.tile.TileI
import io.github.hejcz.engine.Game
import io.github.hejcz.helper.GameScenario
import io.github.hejcz.helper.Players
import io.github.hejcz.helper.TestBasicRemainingTiles
import io.github.hejcz.helper.TestGameSetup
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PutTileValidatorSpec : Spek({

    describe("Putting tile in invalid places") {

        fun singlePlayer(vararg tiles: Tile) =
            Game(
                Players.singlePlayer(), TestGameSetup(TestBasicRemainingTiles(*tiles))
            ).dispatch(BeginCmd)

        it("roads validation") {
            GameScenario(singlePlayer(TileA))
                .then(TileCmd(Position(1, 0), NoRotation))
                .thenReceivedEventShouldBe(InvalidTileLocationEvent)
        }

        it("roads validation") {
            GameScenario(singlePlayer(TileA))
                .then(TileCmd(Position(1, 0), Rotation180))
                .thenReceivedEventShouldBe(InvalidTileLocationEvent)
        }

        it("roads validation") {
            GameScenario(singlePlayer(TileA))
                .then(TileCmd(Position(1, 0), Rotation270))
                .thenReceivedEventShouldBe(InvalidTileLocationEvent)
        }

        it("roads validation") {
            GameScenario(singlePlayer(TileA))
                .then(TileCmd(Position(1, 0), Rotation90)).thenReceivedEventShouldBe(PieceEvent)
        }

        it("Connecting tiles with bounded object") {
            GameScenario(singlePlayer(TileI, TileI))
                .then(TileCmd(Position(0, -1), Rotation180))
                .then(SkipPieceCmd)
                .then(TileCmd(Position(0, -2), Rotation180))
                .thenReceivedEventShouldBe(InvalidTileLocationEvent)
        }
    }
})
