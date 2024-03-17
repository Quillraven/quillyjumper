package com.quillraven.github.quillyjumper.ai

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.component.State
import com.quillraven.github.quillyjumper.util.entityAnimation

data class AiEntity(val entity: Entity, val world: World) {

    fun animation(type: AnimationType) {
        world.entityAnimation(entity, type)
    }

    inline operator fun <reified T : Component<*>> get(type: ComponentType<T>): T = with(world) {
        return entity[type]
    }

    fun state(state: GameObjectState) = with(world) {
        entity[State].fsm.changeState(state)
    }

}
