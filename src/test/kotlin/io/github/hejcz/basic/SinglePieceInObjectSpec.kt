package io.github.hejcz.basic

import io.github.hejcz.core.*
import io.github.hejcz.core.tile.*
import io.github.hejcz.helper.Players
import io.github.hejcz.helper.TestBasicRemainingTiles
import io.github.hejcz.helper.TestGameSetup
import org.amshove.kluent.shouldContain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SinglePieceInObjectSpec : Spek({

    describe("Putting handlers in taken object") {

        fun singlePlayer(vararg tiles: Tile) =
            Game(Players.singlePlayer(), TestGameSetup(TestBasicRemainingTiles(*tiles))).apply { dispatch(Begin) }

        fun multiPlayer(vararg tiles: Tile) =
            Game(Players.twoPlayers(), TestGameSetup(TestBasicRemainingTiles(*tiles))).apply { dispatch(Begin) }

        it("knights") {
            val game = singlePlayer(TileF, TileF)
            game.dispatch(PutTile(Position(0, 1), Rotation90))
            game.dispatch(PutPiece(SmallPiece, Knight(Down)))
            game.dispatch(PutTile(Position(0, 2), Rotation180))
            game.dispatch(PutPiece(SmallPiece, Knight(Down))) shouldContain InvalidPieceLocation
        }

        it("knights two players") {
            val game = multiPlayer(TileF, TileF)
            game.dispatch(PutTile(Position(0, 1), Rotation90))
            game.dispatch(PutPiece(SmallPiece, Knight(Down)))
            game.dispatch(PutTile(Position(0, 2), Rotation180))
            game.dispatch(PutPiece(SmallPiece, Knight(Down))) shouldContain InvalidPieceLocation
        }

        it("brigands") {
            val game = singlePlayer(TileD, TileV)
            game.dispatch(PutTile(Position(1, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Brigand(Right)))
            game.dispatch(PutTile(Position(2, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Brigand(Down))) shouldContain InvalidPieceLocation
        }

        it("brigands two players") {
            val game = multiPlayer(TileD, TileV)
            game.dispatch(PutTile(Position(1, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Brigand(Right)))
            game.dispatch(PutTile(Position(2, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Brigand(Down))) shouldContain InvalidPieceLocation
        }

        it("peasants") {
            val game = singlePlayer(TileD, TileV)
            game.dispatch(PutTile(Position(1, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Peasant(Location(Right, LeftSide))))
            game.dispatch(PutTile(Position(2, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Peasant(Location(Right)))) shouldContain InvalidPieceLocation
        }

        it("peasants two players") {
            val game = multiPlayer(TileD, TileV)
            game.dispatch(PutTile(Position(1, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Peasant(Location(Right, LeftSide))))
            game.dispatch(PutTile(Position(2, 0), NoRotation))
            game.dispatch(PutPiece(SmallPiece, Peasant(Location(Right)))) shouldContain InvalidPieceLocation
        }
    }
})
