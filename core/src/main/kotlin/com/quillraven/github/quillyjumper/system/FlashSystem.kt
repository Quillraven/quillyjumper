package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Flash
import com.quillraven.github.quillyjumper.component.Graphic

class FlashSystem : IteratingSystem(family { all(Flash, Graphic) }) {

    override fun onTickEntity(entity: Entity) {
        val flashCmp = entity[Flash]
        val (_, _, amount, delay, delayTimer) = flashCmp

        if (amount <= 0) {
            // flash is done -> remove it
            entity.configure { it -= Flash }
            return
        }

        if (delayTimer <= 0f) {
            if (flashCmp.doFlash) {
                flashCmp.amount--
            }
            flashCmp.delayTimer = delay
            flashCmp.doFlash = !flashCmp.doFlash
        }
        flashCmp.delayTimer -= deltaTime
    }

}
