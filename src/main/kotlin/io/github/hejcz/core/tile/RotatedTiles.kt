package io.github.hejcz.core.tile

import io.github.hejcz.core.Direction
import io.github.hejcz.core.Directions
import io.github.hejcz.core.Location
import io.github.hejcz.core.Locations

open class TileRotated90(private val tile: Tile) : Tile {
    override fun exploreGreenFields(location: Location): Locations =
        tile.exploreGreenFields(location.left()).map { it.right() }

    override fun hasCloister(): Boolean = tile.hasCloister()
    override fun hasGarden(): Boolean = tile.hasGarden()
    override fun exploreCastle(direction: Direction): Directions =
        tile.exploreCastle(direction.left()).map { it.right() }

    override fun exploreRoad(direction: Direction): Directions =
        tile.exploreRoad(direction.left()).map { it.right() }

    override fun hasEmblem(direction: Direction): Boolean = tile.hasEmblem(direction.left())
}

open class TileRotated180(private val tile: Tile) : Tile {
    override fun exploreGreenFields(location: Location): Locations =
        tile.exploreGreenFields(location.opposite()).map { it.opposite() }

    override fun hasCloister(): Boolean = tile.hasCloister()
    override fun hasGarden(): Boolean = tile.hasGarden()
    override fun exploreCastle(direction: Direction): Directions =
        tile.exploreCastle(direction.opposite()).map { it.opposite() }

    override fun exploreRoad(direction: Direction): Directions =
        tile.exploreRoad(direction.opposite()).map { it.opposite() }

    override fun hasEmblem(direction: Direction): Boolean = tile.hasEmblem(direction.opposite())
}

open class TileRotated270(private val tile: Tile) : Tile {
    override fun exploreGreenFields(location: Location): Locations =
        tile.exploreGreenFields(location.right()).map { it.left() }

    override fun hasCloister(): Boolean = tile.hasCloister()
    override fun hasGarden(): Boolean = tile.hasGarden()
    override fun exploreCastle(direction: Direction): Directions =
        tile.exploreCastle(direction.right()).map { it.left() }

    override fun exploreRoad(direction: Direction): Directions =
        tile.exploreRoad(direction.right()).map { it.left() }

    override fun hasEmblem(direction: Direction): Boolean = tile.hasEmblem(direction.right())
}
