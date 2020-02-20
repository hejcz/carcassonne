package io.github.hejcz.expansion.inn

import io.github.hejcz.core.*
import io.github.hejcz.engine.setup.Extension
import io.github.hejcz.engine.setup.PiecesSetup
import io.github.hejcz.engine.setup.TilesSetup
import io.github.hejcz.expansion.inn.tiles.*
import io.github.hejcz.engine.setup.*

object InnAndCathedralsExtension : Extension {
    override fun modify(piecesSetup: PiecesSetup) = piecesSetup.add(BigPiece)

    override fun modify(deck: TilesSetup) {
        deck.addAndShuffle(TileEA, TileEB, TileEC, TileED, TileEE, TileEF, TileEG, TileEH, TileEI, TileEJ,
            TileEK, TileEL, TileEM, TileEN, TileEO, TileEP, TileEQ)
    }
}
