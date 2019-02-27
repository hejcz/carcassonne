package io.github.hejcz.tiles.river

import io.github.hejcz.placement.Direction
import io.github.hejcz.tiles.basic.TileRotated270

class RiverTileRotated270(private val tile: RiverTile) : TileRotated270(tile), RiverTile {
    override fun exploreRiver(): Collection<Direction> = tile.exploreRiver().map { it.left() }
}
