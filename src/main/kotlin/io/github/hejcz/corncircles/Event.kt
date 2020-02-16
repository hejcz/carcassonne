package io.github.hejcz.corncircles

import io.github.hejcz.core.*
import io.github.hejcz.util.*

enum class CornCircleAction {
    ADD_PIECE, REMOVE_PIECE
}

data class AddPieceCmd(val position: Position, val piece: Piece, val role: Role) : Command

object AvoidCornCircleActionCmd : Command

data class ChooseCornCircleActionCmd(val action: CornCircleAction) : Command

data class RemovePieceCmd(val position: Position, val piece: Piece, val role: Role) : Command