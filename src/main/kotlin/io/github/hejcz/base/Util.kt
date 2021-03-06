package io.github.hejcz.base

import io.github.hejcz.api.*

inline fun <reified T> commandValidator(crossinline handler: (state: State, command: T) -> Collection<GameEvent>) =
    object : CmdValidator {
        override fun validate(state: State, command: Command): Collection<GameEvent> =
            (command as? T)?.let { handler(state, command) } ?: emptySet()
    }

inline fun <reified T : Command> eventsHandler(): CmdHandler {
    return object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is T
        override fun apply(state: State, command: Command): State = state
    }
}

inline fun <reified T : Command> eventsHandler(crossinline body: (state: State, command: T) -> State): CmdHandler {
    return object : CmdHandler {
        override fun isApplicableTo(command: Command): Boolean = command is T
        override fun apply(state: State, command: Command): State = body(state, command as T)
    }
}
