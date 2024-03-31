package com.quillraven.github.quillyjumper.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.util.entityAnimation
import kotlin.math.atan2

data class AiEntity(val entity: Entity, val world: World) {

    fun animation(type: AnimationType, playMode: PlayMode = PlayMode.LOOP) {
        world.entityAnimation(entity, type, playMode)
    }

    inline operator fun <reified T : Component<*>> get(type: ComponentType<T>): T = with(world) {
        return entity[type]
    }

    fun state(state: GameObjectState) = with(world) {
        entity[State].fsm.changeState(state)
    }

    fun aggroTarget(): Entity? = with(world) {
        val (aggroEntities) = entity[Aggro]
        if (aggroEntities.isEmpty()) {
            return null
        }

        return aggroEntities.firstOrNull { it has EntityTag.PLAYER }
    }

    fun notInRange(location: Vector2, tolerance: Float): Boolean = with(world) {
        val (_, center) = entity[Graphic]
        val diffX = location.x - center.x
        val diffY = location.y - center.y

        return (diffX * diffX) + (diffY * diffY) > tolerance * tolerance
    }

    fun inRange(location: Vector2, tolerance: Float): Boolean = with(world) {
        val (_, center) = entity[Graphic]
        val diffX = location.x - center.x
        val diffY = location.y - center.y

        return (diffX * diffX) + (diffY * diffY) <= tolerance * tolerance
    }

    fun inRange(target: Entity, tolerance: Float): Boolean = with(world) {
        return inRange(target[Graphic].center, tolerance)
    }

    fun angleTo(target: Entity): Float = with(world) {
        val (_, center) = entity[Graphic]
        val (_, targetCenter) = target[Graphic]

        return atan2(targetCenter.y - center.y, targetCenter.x - center.x)
    }

}
