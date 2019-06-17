package io.github.hejcz.core.rule

import io.github.hejcz.core.*
import io.github.hejcz.core.tile.*

object RewardIncompleteCloister : EndRule {
    override fun apply(state: State): Collection<GameEvent> =
        state.allMonks().map { (playerId, piece) -> PlayerScored(playerId, score(state, piece.position), emptySet()) }

    private fun score(state: State, cloisterPosition: Position): Int =
        1 + cloisterPosition.surrounding().count { state.tileAt(it) !is NoTile }
}