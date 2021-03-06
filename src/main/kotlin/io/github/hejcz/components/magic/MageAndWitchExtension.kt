package io.github.hejcz.components.magic

import io.github.hejcz.api.*
import io.github.hejcz.base.*
import io.github.hejcz.setup.*
import io.github.hejcz.base.commandValidator
import io.github.hejcz.base.eventsHandler

object MageAndWitchExtension : Extension {

    override fun modify(deck: TilesSetup) {
        deck.addAndShuffle(MaHeA, MaHeB, MaHeC, MaHeD, MaHeE, MaHeF, MaHeG, MaHeH)
    }

    override fun modify(setup: ValidatorsSetup) {
        setup.add(MoveMageOrWitchCommandValidator)
        setup.add(PickUpMageOrWitchValidator)
    }

    override fun modify(setup: CommandHandlersSetup) {
        setup.add(MoveMageOrWitchCommandHandler)
        setup.add(PickUpMageOrWitchHandler)
    }

    override fun modify(setup: StateExtensionSetup) {
        setup.add(StateExt())
    }

    private val MoveMageOrWitchCommandHandler =
        eventsHandler<MoveMageOrWitchCmd> { state, command ->
            state.update(
                extractWitchState(state)
                    .addNpcPiece(command.magicTarget, command.position, command.direction)
            )
        }

    private val MoveMageOrWitchCommandValidator =
        commandValidator<MoveMageOrWitchCmd> { state, command ->
            if (extractWitchState(state).canBePlacedOn(
                    state,
                    command.magicTarget,
                    PositionedDirection(command.position, command.direction)
                )
            ) {
                emptySet()
            } else {
                setOf(InvalidPieceLocationEvent)
            }
        }

    private val PickUpMageOrWitchHandler =
        eventsHandler<PickUpMageOrWitchCmd>()

    private val PickUpMageOrWitchValidator =
        commandValidator<PickUpMageOrWitchCmd> { state, command ->
            if (extractWitchState(state).canBePickedUp(state, command.magicTarget)) {
                emptySet()
            } else {
                setOf(CantPickUpPieceEvent(command.magicTarget))
            }
        }

    private fun extractWitchState(state: State) = (state.get(
        MageAndWitchState.ID) as StateExt)

    private data class StateExt(
        private val mage: PositionedDirection? = null,
        private val witch: PositionedDirection? = null
    ) : StateExtension, MageAndWitchState {
        override fun id(): StateExtensionId = MageAndWitchState.ID

        override fun getMagePosition(): PositionedDirection? = mage

        override fun getWitchPosition(): PositionedDirection? = witch

        /**
         * When mage and witch are on the same object.
         */
        override fun mageOrWitchMustBeInstantlyMoved(state: State): Boolean {
            if (mage == null || witch == null) {
                return false
            }
            val isMageOnCastle = state.tileAt(mage.position).exploreCastle(mage.direction).isNotEmpty()
            val isWitchOnCastle = state.tileAt(witch.position).exploreCastle(witch.direction).isNotEmpty()
            return when {
                isMageOnCastle != isWitchOnCastle -> false
                isMageOnCastle -> CastlesExplorer.explore(state, mage.position, mage.direction).first.contains(
                    PositionedDirection(witch.position, witch.direction)
                )
                else -> RoadsExplorer.explore(state, mage.position, mage.direction).first.contains(
                    PositionedDirection(witch.position, witch.direction)
                )
            }
        }

        fun addNpcPiece(piece: MagicTarget, position: Position, direction: Direction): StateExt =
            putNPC(piece, position, direction)

        /**
         * Only when there is no open object or the only one is taken be another piece.
         */
        fun canBePickedUp(state: State, piece: MagicTarget): Boolean {
            val (target, other) = when (piece) {
                Mage -> mage to witch
                Witch -> witch to mage
            }
            if (target == null) {
                return false
            }
            val openPositions = state.findOpenCastles() + state.findOpenRoads()
            val actualPositions = exploreRoadOrCastle(state, target)
            val otherPositions = other?.let { exploreRoadOrCastle(state, it) } ?: emptySet()
            val allowedPositions = openPositions - actualPositions - otherPositions
            return allowedPositions.isEmpty()
        }

        /**
         * This must be an open object without the other piece on it.
         */
        fun canBePlacedOn(state: State, piece: MagicTarget, targetPos: PositionedDirection): Boolean {
            val (target, other) = when (piece) {
                Mage -> mage to witch
                Witch -> witch to mage
            }
            val openCastles = state.findOpenCastles()
            val openRoads = state.findOpenRoads()
            val isOpenObject = openCastles.contains(targetPos) || openRoads.contains(targetPos)
            if (target == null && other == null) {
                return isOpenObject
            }
            if (target != null && exploreRoadOrCastle(state, target).contains(targetPos)) {
                return false
            }
            if (other != null && exploreRoadOrCastle(state, other).contains(targetPos)) {
                return false
            }
            return isOpenObject
        }

        private fun putNPC(piece: MagicTarget, position: Position, direction: Direction): StateExt = when (piece) {
            Mage -> copy(mage = PositionedDirection(position, direction))
            Witch -> copy(witch = PositionedDirection(position, direction))
        }

        private fun exploreRoadOrCastle(state: State, npc: PositionedDirection): Set<PositionedDirection> {
            return when {
                state.tileAt(npc.position).exploreCastle(npc.direction).isNotEmpty() ->
                    CastlesExplorer.explore(state, npc.position, npc.direction).first
                else ->
                    RoadsExplorer.explore(state, npc.position, npc.direction).first
            }
        }
    }
}
