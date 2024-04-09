package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.AnimationService.Companion.updateRegion
import com.quillraven.github.quillyjumper.component.Animation
import com.quillraven.github.quillyjumper.component.Animation.Companion.GLOBAL_ANIMATION
import com.quillraven.github.quillyjumper.component.Animation.Companion.NORMAL_ANIMATION
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Remove
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.PlayerItemCollectEvent

class AnimationSystem(
    private val animationService: AnimationService = inject(),
) : IteratingSystem(World.family { all(NORMAL_ANIMATION, Graphic) }), GameEventListener {

    override fun onTickEntity(entity: Entity) {
        val globalAniCmp = entity.getOrNull(GLOBAL_ANIMATION)
        if (globalAniCmp != null) {
            // update global animation and remove component when it is done
            entity.updateAnimation(globalAniCmp)
            if (globalAniCmp.gdxAnimation.isAnimationFinished(globalAniCmp.timer)) {
                entity.configure { it -= GLOBAL_ANIMATION }
            }
            return
        }

        // update normal animation
        entity.updateAnimation(entity[NORMAL_ANIMATION])
    }

    private fun Entity.updateAnimation(animationCmp: Animation) {
        val (gdxAnimation, playMode, timer) = animationCmp
        val (sprite) = this[Graphic]

        gdxAnimation.playMode = playMode
        sprite.updateRegion(gdxAnimation.getKeyFrame(timer))
        animationCmp.timer += deltaTime
    }

    override fun onEvent(event: GameEvent) = with(animationService) {
        if (event is PlayerItemCollectEvent) {
            // change player animation to double jump
            world.entityAnimation(event.player, AnimationType.DOUBLE_JUMP, PlayMode.NORMAL, GLOBAL_ANIMATION)

            // change collectable entity animation to "collected" and remove it at the end of the animation
            val collectedAnimation = gdxAnimation("collected", AnimationType.IDLE)
            event.collectable[NORMAL_ANIMATION].changeAnimation(collectedAnimation, PlayMode.NORMAL)
            event.collectable.configure {
                it += Remove(time = collectedAnimation.animationDuration, removePhysic = true)
            }
        }
    }
}
