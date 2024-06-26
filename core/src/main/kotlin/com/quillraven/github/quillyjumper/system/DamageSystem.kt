package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.SoundAsset
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.component.Animation.Companion.GLOBAL_ANIMATION
import com.quillraven.github.quillyjumper.component.EntityTag.PLAYER
import com.quillraven.github.quillyjumper.event.EntityDamageEvent
import com.quillraven.github.quillyjumper.event.GameEventDispatcher
import com.quillraven.github.quillyjumper.event.PlayerMapBottomContactEvent
import ktx.log.logger

class DamageSystem(
    private val audioService: AudioService = inject(),
    private val animationService: AnimationService = inject()
) : IteratingSystem(family { all(DamageTaken, Life, PLAYER).none(Invulnerable) }) {

    override fun onTickEntity(entity: Entity) = with(animationService) {
        val (damageAmount) = entity[DamageTaken]
        val lifeCmp = entity[Life]
        lifeCmp.current = (lifeCmp.current - damageAmount).coerceAtLeast(0f)
        log.debug { "Entity $entity takes $damageAmount damage. New life=${lifeCmp.current}" }

        if (lifeCmp.current <= 0) {
            // the event will trigger the TiledService that contains the logic for respawning
            GameEventDispatcher.fire(PlayerMapBottomContactEvent(entity))
        } else {
            audioService.play(SoundAsset.HURT)
            GameEventDispatcher.fire(EntityDamageEvent(entity, lifeCmp))

            // player becomes invulnerable after taking damage
            entity.configure {
                it += Invulnerable(1.5f)
                it += Blink(maxTime = 1.5f, blinkRatio = 0.1f)
                it += Flash(color = Color.RED, weight = 0.75f, amount = 1, delay = 0.15f)
                world.entityAnimation(it, AnimationType.HIT, PlayMode.NORMAL, GLOBAL_ANIMATION)
            }
        }
    }

    companion object {
        private val log = logger<DamageSystem>()
    }

}
