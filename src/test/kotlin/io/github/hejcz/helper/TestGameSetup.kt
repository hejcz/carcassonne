package io.github.hejcz.helper

import io.github.hejcz.base.CoreModule
import io.github.hejcz.setup.Extension
import io.github.hejcz.setup.GameSetup
import io.github.hejcz.api.Tile
import io.github.hejcz.components.abbot.AbbotExtension
import io.github.hejcz.components.corncircles.CornCirclesExtension
import io.github.hejcz.components.inn.InnAndCathedralsExtension
import io.github.hejcz.components.magic.MageAndWitchExtension
import io.github.hejcz.components.river.RiverExtension

interface RemainingTiles {
    fun tiles(): List<Tile>
}

open class TestGameSetup(
    private val remainingTiles: RemainingTiles,
    extensions: List<Extension>
) : GameSetup(CoreModule, *extensions.toTypedArray()) {
    constructor(remainingTiles: RemainingTiles) : this(remainingTiles, emptyList())

    override fun tiles(): List<Tile> = remainingTiles.tiles()
}

class RiverTestGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(RiverExtension))

class AbbotTestGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(AbbotExtension))

class InnAndCathedralsTestGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(InnAndCathedralsExtension))

class CornCirclesGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(CornCirclesExtension))

class WitchAndMageGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(MageAndWitchExtension))

class WitchAndMageAndInnAndCathedralsGameSetup(remainingTiles: RemainingTiles) :
    TestGameSetup(remainingTiles, listOf(InnAndCathedralsExtension, MageAndWitchExtension))
