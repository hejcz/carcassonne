package io.github.hejcz.expansion.corncircles

import io.github.hejcz.core.*
import io.github.hejcz.setup.*

object CornCirclesExtension : Extension {

    override fun modify(validatorsSetup: ValidatorsSetup) {
        validatorsSetup.add(AvoidCornCircleActionValidator)
        validatorsSetup.add(AddPieceValidator)
        validatorsSetup.add(RemovePieceValidator)
    }

    override fun modify(commandHandlersSetup: CommandHandlersSetup) {
        commandHandlersSetup.add(AddPieceHandler)
        commandHandlersSetup.add(RemovePieceHandler)
        commandHandlersSetup.add(ChooseCornCircleActionHandler)
        commandHandlersSetup.add(AvoidCornCircleActionHandler)
    }

    override fun modify(deck: TilesSetup) {
        deck.addAndShuffle(Korn1, Korn2, Korn3, Korn4, Korn5, Korn6)
    }

    override fun modify(stateExtensionSetup: StateExtensionSetup) {
        stateExtensionSetup.add(StateExt(CornCircleAction.ADD_PIECE))
    }

    private data class StateExt(val selected: CornCircleAction) : StateExtension {
        override fun id(): StateExtensionId = ID

        fun makeDecision(state: State, action: CornCircleAction): State =
            state.update(copy(selected = action))

        fun isSelected(action: CornCircleAction) = action == selected

        fun currentPlayerPieces(state: State, symbol: CornSymbol) = when (symbol) {
            KnightSymbol -> state.allOf(Knight::class, state.currentPlayerId())
            BrigandSymbol -> state.allOf(Brigand::class, state.currentPlayerId())
            PeasantSymbol -> state.allOf(Peasant::class, state.currentPlayerId())
        }

        companion object {
            val ID = StateExtensionId(
                CornCirclesExtension::class.java.simpleName
            )
        }
    }

    private fun State.cornState() = this.get(StateExt.ID)!! as StateExt

    private val AvoidCornCircleActionHandler = object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is AvoidCornCircleActionCmd

        override fun apply(state: State, command: Command): State = state
    }

    private val AvoidCornCircleActionValidator =
        commandValidator<AvoidCornCircleActionCmd> { state, _ ->
            when (val tile = state.recentTile()) {
                is CornCircleTile -> when {
                    state.cornState()
                        .currentPlayerPieces(state, tile.cornCircleEffect())
                        .isNotEmpty() -> setOf(CantSkipPieceEvent)
                    else -> emptySet()
                }
                else -> emptySet()
            }
        }

    private val AddPieceHandler = object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is AddPieceCmd

        override fun apply(state: State, command: Command): State =
            (command as AddPieceCmd).let {
                state.addPiece(command.position, command.piece, command.role)
            }
    }

    private val AddPieceValidator =
        commandValidator<AddPieceCmd> { state, command ->
            when (val tile = state.recentTile()) {
                is CornCircleTile -> when {
                    !tile.cornCircleEffect().matches(command.role) -> setOf(InvalidPieceLocationEvent)
                    state.cornState().isSelected(CornCircleAction.REMOVE_PIECE) ->
                        setOf(PlayerSelectedOtherCornAction)
                    playerDoesNotHaveAnyPieceThere(state, command) -> setOf(InvalidPieceLocationEvent)
                    !state.isAvailableForCurrentPlayer(command.piece) -> setOf(NoMeepleEvent(command.piece))
                    else -> emptySet()
                }
                else -> emptySet()
            }
        }

    private val ChooseCornCircleActionHandler = object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is ChooseCornCircleActionCmd

        override fun apply(state: State, command: Command): State = state.cornState()
            .makeDecision(state, (command as ChooseCornCircleActionCmd).action)
    }

    private fun playerDoesNotHaveAnyPieceThere(state: State, command: AddPieceCmd) =
        state.findPieces(command.position, command.role)
            .none { state.currentPlayerId() == it.playerId }

    private val RemovePieceHandler = object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is RemovePieceCmd

        override fun apply(state: State, command: Command): State =
            (command as RemovePieceCmd).let {
                state.removePiece(command.position, command.piece, command.role)
            }
    }

    private val RemovePieceValidator =
        commandValidator<RemovePieceCmd> { state, command ->
            when (val tile = state.recentTile()) {
                is CornCircleTile -> when {
                    !tile.cornCircleEffect().matches(command.role) -> setOf(InvalidPieceLocationEvent)
                    state.cornState().isSelected(CornCircleAction.ADD_PIECE) ->
                        setOf(PlayerSelectedOtherCornAction)
                    playerDoesNotHaveSuchPieceThere(state, command) -> setOf(InvalidPieceLocationEvent)
                    else -> emptySet()
                }
                else -> emptySet()
            }
        }

    private fun playerDoesNotHaveSuchPieceThere(state: State, command: RemovePieceCmd) =
        state.findPieces(command.position, command.role)
            .none { state.currentPlayerId() == it.playerId && it.pieceOnBoard.piece == command.piece }
}
