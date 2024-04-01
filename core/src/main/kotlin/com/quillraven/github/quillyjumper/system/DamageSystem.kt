package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.SoundAsset
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.event.EntityDamageEvent
import com.quillraven.github.quillyjumper.event.GameEventDispatcher
import ktx.log.logger

class DamageSystem(
    private val audioService: AudioService = inject(),
) : IteratingSystem(family { all(DamageTaken, Life).none(Invulnerable) }) {

    override fun onTickEntity(entity: Entity) {
        val (damageAmount) = entity[DamageTaken]
        val lifeCmp = entity[Life]
        lifeCmp.current = (lifeCmp.current - damageAmount).coerceAtLeast(0f)
        log.debug { "Entity $entity takes $damageAmount damage. New life=${lifeCmp.current}" }
        audioService.play(SoundAsset.HURT)
        GameEventDispatcher.fire(EntityDamageEvent(entity, lifeCmp))

        if (entity has EntityTag.PLAYER) {
            // player becomes invulnerable after taking damage
            entity.configure {
                it += Invulnerable(1.5f)
                it += Blink(maxTime = 1.5f, blinkRatio = 0.1f)
                it += Flash(color = Color.RED, weight = 0.75f, amount = 1, delay = 0.15f)
                it += Animation(
                    gdxAnimation = gdxAnimation(world, it[Tiled].gameObject, AnimationType.HIT),
                    type = Animation.GLOBAL_ANIMATION,
                    playMode = PlayMode.NORMAL
                )
            }
        }
    }

    companion object {
        private val log = logger<DamageSystem>()
    }

}
