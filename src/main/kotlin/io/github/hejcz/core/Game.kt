package io.github.hejcz.core

import io.github.hejcz.setup.*

class Game(players: Collection<Player>, gameSetup: GameSetup) {

    val state: State
    val rules: Collection<Rule>
    val endRules: Collection<EndRule>

    private val validators: Collection<CommandValidator>
    private val handlers: Collection<CommandHandler>

    init {
        val tiles = gameSetup.tiles()
        val board = Board(mapOf(Position(0, 0) to tiles.next()))
        state = State(players.mapTo(mutableSetOf()) { playerWithPieces(it, gameSetup) }, tiles, board)
        validators = gameSetup.validators()
        rules = gameSetup.rules()
        endRules = gameSetup.endRules()
        handlers = gameSetup.handlers()
    }

    private fun playerWithPieces(player: Player, gameSetup: GameSetup) = when {
        player.initialPieces.isNotEmpty() -> player
        else -> Player(player.id, player.order, gameSetup.pieces())
    }

    fun dispatch(command: Command): Collection<GameEvent> {
        val errors = validate(command)
        return when {
            errors.isNotEmpty() -> errors
            else -> handlers.firstOrNull { it.isApplicableTo(command) }?.handle(this, command) ?: setOf(InvalidCommand)
        }
    }

    private fun validate(command: Command) =
        validators.asSequence().map { it.validate(state, command) }.firstOrNull { it.isNotEmpty() }?.toSet()
            ?: emptySet()
}
