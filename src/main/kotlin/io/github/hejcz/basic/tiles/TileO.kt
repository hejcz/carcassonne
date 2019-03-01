package io.github.hejcz.basic.tiles

import io.github.hejcz.core.*
import io.github.hejcz.core.*
object TileO : Tile, GreenFieldExplorable by RegionGreenFieldExplorable(
    setOf(Location(Down, RightSide), Location(Right, LeftSide)),
    setOf(Location(Down, LeftSide), Location(Right, RightSide))
) {
    override fun hasEmblem(): Boolean = true
    override fun exploreCastle(direction: Direction): Directions = setOf(Up, Left)
    override fun exploreRoad(direction: Direction) = emptySet<Direction>()
}