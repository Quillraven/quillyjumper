package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Blink
import com.quillraven.github.quillyjumper.component.Graphic

class BlinkSystem : IteratingSystem(family { all(Blink, Graphic) }) {

    override fun onTickEntity(entity: Entity) {
        val blinkCmp = entity[Blink]
        val (maxTime, blinkRatio) = blinkCmp
        val (sprite) = entity[Graphic]

        if (maxTime <= 0f) {
            // blinking is done -> remove it
            sprite.setAlpha(1f)
            entity.configure { it -= Blink }
            return
        }

        blinkCmp.maxTime -= deltaTime
        blinkCmp.timer += deltaTime
        if (blinkCmp.timer >= blinkRatio) {
            blinkCmp.timer = 0f
            sprite.setAlpha(if (sprite.color.a == 0f) 1f else 0f)
        }
    }

}
