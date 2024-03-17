package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.StateMachine
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.quillraven.github.quillyjumper.ai.AiEntity
import com.quillraven.github.quillyjumper.ai.GameObjectState

class State(
    owner: AiEntity,
    initialState: GameObjectState,
    val fsm: StateMachine<AiEntity, GameObjectState> = DefaultStateMachine(owner, initialState)
) : Component<State> {
    override fun type() = State

    companion object : ComponentType<State>()
}
