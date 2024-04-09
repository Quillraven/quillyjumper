package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.AnimationService.Companion.updateRegion
import com.quillraven.github.quillyjumper.GameObject.CHERRY
import com.quillraven.github.quillyjumper.GameObject.FINISH_FLAG
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.component.Animation.Companion.GLOBAL_ANIMATION
import com.quillraven.github.quillyjumper.component.Animation.Companion.NORMAL_ANIMATION
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
        val aniCmp = entity[NORMAL_ANIMATION]
        val nextGdxAnimation = aniCmp.nextAnimation
        if (nextGdxAnimation != null && aniCmp.gdxAnimation.isAnimationFinished(aniCmp.timer)) {
            // change to the next animation and replace current animation
            aniCmp.nextAnimation = null
            aniCmp.changeAnimation(nextGdxAnimation)
            return
        }

        // update current animation
        entity.updateAnimation(aniCmp)
    }

    private fun Entity.updateAnimation(animationCmp: Animation) {
        val (gdxAnimation, playMode, timer) = animationCmp
        val (sprite) = this[Graphic]

        gdxAnimation.playMode = playMode
        sprite.updateRegion(gdxAnimation.getKeyFrame(timer))
        animationCmp.timer += deltaTime
    }

    override fun onEvent(event: GameEvent) {
        if (event !is PlayerItemCollectEvent) {
            return
        }

        when (event.collectableType) {
            CHERRY -> onCollectCherry(event.player, event.collectable)
            FINISH_FLAG -> onCollectFinishFlag(event.collectable)
            else -> Unit
        }
    }

    private fun onCollectCherry(player: Entity, cherry: Entity) = with(animationService) {
        // change player animation to double jump
        world.entityAnimation(player, AnimationType.DOUBLE_JUMP, PlayMode.NORMAL, GLOBAL_ANIMATION)

        // change collectable entity animation to "collected" and remove it at the end of the animation
        val collectedAnimation = gdxAnimation("collected", AnimationType.IDLE)
        cherry[NORMAL_ANIMATION].changeAnimation(collectedAnimation, PlayMode.NORMAL)
        cherry.configure {
            it += Remove(time = collectedAnimation.animationDuration, removePhysic = true)
        }
    }

    private fun onCollectFinishFlag(finishFlag: Entity) = with(animationService) {
        world.entityAnimation(finishFlag, AnimationType.RUN, PlayMode.NORMAL, NORMAL_ANIMATION).also {
            it.nextAnimation = gdxAnimation(FINISH_FLAG, AnimationType.WAVE)
        }
        finishFlag.configure { it -= EntityTag.COLLECTABLE }
    }
}
