package io.github.hejcz.basic.tiles
import io.github.hejcz.core.*
object TileN : Tile {
    override fun exploreGreenFields(location: Location): Locations = setOf(
        Location(Left),
        Location(Down)
    )

    override fun exploreCastle(direction: Direction): Directions = setOf(
        Up,
        Right
    )

    override fun exploreRoad(direction: Direction) = emptySet<Direction>()
}