package io.github.hejcz.expansion.corncircles

import io.github.hejcz.core.*
import io.github.hejcz.core.tile.*

enum class CornSymbol(val matches: (Role) -> Boolean) {
    KNIGHT({ it is Knight }), BRIGAND({ it is Brigand }), PEASANT({ it is Peasant })
}

interface CornCircleTile : Tile {
    fun cornCircleEffect(): CornSymbol

    override fun rotate(rotation: Rotation): Tile = when (rotation) {
        NoRotation -> this
        Rotation90 -> CornCircleRotated90(this)
        Rotation180 -> CornCircleRotated180(this)
        Rotation270 -> CornCircleRotated270(this)
    }

    private class CornCircleRotated90(private val tile: CornCircleTile) : TileRotated90(tile), CornCircleTile {
        override fun cornCircleEffect(): CornSymbol = tile.cornCircleEffect()
    }

    private class CornCircleRotated180(private val tile: CornCircleTile) : TileRotated180(tile), CornCircleTile {
        override fun cornCircleEffect(): CornSymbol = tile.cornCircleEffect()
    }

    private class CornCircleRotated270(private val tile: CornCircleTile) : TileRotated270(tile), CornCircleTile {
        override fun cornCircleEffect(): CornSymbol = tile.cornCircleEffect()
    }
}

object Korn1 : CornCircleTile, TileWithGreenFields by GreenFields(
    setOf(Location(Left, LeftSide), Location(Down, RightSide)),
    setOf(Location(Right, RightSide), Location(Down, LeftSide)),
    setOf(Location(Right, LeftSide), Location(Left, RightSide), Location(Up))
) {
    override fun cornCircleEffect(): CornSymbol = CornSymbol.BRIGAND

    override fun exploreCastle(direction: Direction): Directions = emptySet()

    override fun exploreRoad(direction: Direction): Directions = when (direction) {
        Right -> setOf(Right)
        Down, Left -> setOf(Down, Left)
        else -> emptySet()
    }
}

object Korn2 : CornCircleTile, TileWithGreenFields by GreenFields(
    setOf(Location(Left, LeftSide), Location(Down, RightSide)),
    setOf(Location(Right, RightSide), Location(Down, LeftSide), Location(Left, RightSide), Location(Up, LeftSide)),
    setOf(Location(Right, LeftSide), Location(Up, RightSide))
) {
    override fun cornCircleEffect(): CornSymbol = CornSymbol.BRIGAND

    override fun exploreCastle(direction: Direction): Directions = emptySet()

    override fun exploreRoad(direction: Direction): Directions = when (direction) {
        Right, Up -> setOf(direction)
        Down, Left -> setOf(Down, Left)
        else -> emptySet()
    }
}

object Korn3 : CornCircleTile {
    override fun exploreGreenFields(location: Location): Locations =
        setOf(Location(Left), Location(Right), Location(Down, RightSide), Location(Down, LeftSide))

    override fun cornCircleEffect(): CornSymbol = CornSymbol.PEASANT

    override fun exploreCastle(direction: Direction): Directions = direction.sameIf(Up)

    override fun exploreRoad(direction: Direction): Directions = direction.sameIf(Down)
}

object Korn4 : CornCircleTile, TileWithGreenFields by GreenFields(
    setOf(Location(Left, LeftSide), Location(Right, RightSide)),
    setOf(Location(Right, LeftSide), Location(Left, RightSide))
) {
    override fun cornCircleEffect(): CornSymbol = CornSymbol.PEASANT

    override fun exploreCastle(direction: Direction): Directions = direction.sameIf(Up)

    override fun exploreRoad(direction: Direction): Directions = emptySet()
}

object Korn5 : CornCircleTile, TileWithGreenFields by GreenFields(
    setOf(Location(Left)),
    setOf(Location(Right))
) {
    override fun cornCircleEffect(): CornSymbol = CornSymbol.KNIGHT

    override fun exploreCastle(direction: Direction): Directions = direction.sameIfOneOf(Up, Down)

    override fun exploreRoad(direction: Direction): Directions = emptySet()
}

object Korn6 : CornCircleTile, TileWithGreenFields by GreenFields(
    setOf(Location(Left)),
    setOf(Location(Right))
) {
    override fun cornCircleEffect(): CornSymbol = CornSymbol.KNIGHT

    override fun exploreCastle(direction: Direction): Directions = when (direction) {
        Up, Left -> setOf(Up, Left)
        Down, Right -> setOf(Down, Right)
        else -> emptySet()
    }

    override fun exploreRoad(direction: Direction): Directions = emptySet()
}