package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.AnimationService.Companion.updateRegion
import com.quillraven.github.quillyjumper.component.Animation
import com.quillraven.github.quillyjumper.component.Animation.Companion.GLOBAL_ANIMATION
import com.quillraven.github.quillyjumper.component.Animation.Companion.NORMAL_ANIMATION
import com.quillraven.github.quillyjumper.component.Graphic

class AnimationSystem : IteratingSystem(World.family { all(NORMAL_ANIMATION, Graphic) }) {

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

}
