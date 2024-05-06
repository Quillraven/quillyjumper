package com.quillraven.github.quillyjumper.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.component.*
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import kotlin.math.atan2

data class AiEntity(
    val entity: Entity,
    val world: World,
    private val animationService: AnimationService,
    private val physicWorld: PhysicWorld,
) {

    fun animation(type: AnimationType, playMode: PlayMode = PlayMode.LOOP) = with(animationService) {
        world.entityAnimation(entity, type, playMode, Animation.NORMAL_ANIMATION)
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
        val center = entity[Graphic].center
        val diffX = location.x - center.x
        val diffY = location.y - center.y

        return (diffX * diffX) + (diffY * diffY) > tolerance * tolerance
    }

    fun inRange(location: Vector2, tolerance: Float): Boolean = with(world) {
        val center = entity[Graphic].center
        val diffX = location.x - center.x
        val diffY = location.y - center.y

        return (diffX * diffX) + (diffY * diffY) <= tolerance * tolerance
    }

    fun inRange(target: Entity, tolerance: Float): Boolean = with(world) {
        return inRange(target[Graphic].center, tolerance)
    }

    fun angleTo(target: Entity): Float = with(world) {
        val center = entity[Graphic].center
        val targetCenter = target[Graphic].center

        return atan2(targetCenter.y - center.y, targetCenter.x - center.x)
    }

    fun isPathBlocked(target: Entity): Boolean = with(world) {
        val start = entity[Graphic].center
        val end = target[Graphic].center
        var blocked = false

        physicWorld.rayCast(start, end) { fixture, _, _, _ ->
            if (fixture.body.type == BodyDef.BodyType.StaticBody) {
                // path to target is blocked by ground/wall
                blocked = true
                return@rayCast RayCast.TERMINATE
            }

            // ignore fixture and continue
            return@rayCast RayCast.IGNORE
        }

        return blocked
    }

}
