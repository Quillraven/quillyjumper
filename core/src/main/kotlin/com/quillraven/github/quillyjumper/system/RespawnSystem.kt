package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.component.Animation.Companion.GLOBAL_ANIMATION
import com.quillraven.github.quillyjumper.component.Animation.Companion.NORMAL_ANIMATION
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.PlayerItemCollectEvent

class RespawnSystem(
    private val animationService: AnimationService = inject()
) : IteratingSystem(family { all(Respawn) }), GameEventListener {

    override fun onTickEntity(entity: Entity) {
        val respawnCmp = entity[Respawn]
        respawnCmp.time -= deltaTime
        if (respawnCmp.time <= 0.0) {
            respawnCmp.action?.invoke(world)
            entity.configure { it -= Respawn }
        }
    }

    override fun onEvent(event: GameEvent) {
        if (event !is PlayerItemCollectEvent || event.collectableType != GameObject.CHERRY) {
            return
        }

        val cherry = event.collectable
        val respawnAction: RespawnAction = {
            cherry.configure {
                val normalAniCmp = cherry[NORMAL_ANIMATION]
                val collectedAnimation = normalAniCmp.gdxAnimation

                // play reversed collected animation
                val globalAniCmp = cherry.getOrAdd(GLOBAL_ANIMATION) {
                    Animation(collectedAnimation, PlayMode.REVERSED, type = GLOBAL_ANIMATION)
                }
                globalAniCmp.changeAnimation(collectedAnimation, PlayMode.REVERSED)
                // play looped idle animation when collected animation is done
                val idleAnimation = animationService.gdxAnimation(GameObject.CHERRY, AnimationType.IDLE)
                normalAniCmp.changeAnimation(idleAnimation, PlayMode.LOOP)
                // add collectable tag to trigger collect events again
                it += EntityTag.COLLECTABLE
            }
        }

        event.collectable.configure { entity ->
            // remove collectable tag to not trigger collect event again and again
            entity -= EntityTag.COLLECTABLE
            entity += Respawn(3f, respawnAction)
        }
    }
}
