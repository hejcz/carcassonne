package io.github.hejcz.core.validator

import io.github.hejcz.core.*
import io.github.hejcz.corncircles.*

object PieceAvailabilityValidator : CommandValidator {
    override fun validate(state: State, command: Command): Collection<GameEvent> = when (command) {
        is PutPiece -> isAvailable(state, command.piece)
        is AddPieceCommand -> isAvailable(state, command.piece)
        else -> emptySet()
    }

    private fun isAvailable(state: State, piece: Piece) =
        if (state.isAvailableForCurrentPlayer(piece)) emptySet() else setOf(NoMappleAvailable(piece))
}
