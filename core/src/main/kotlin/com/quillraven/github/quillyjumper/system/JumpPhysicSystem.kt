package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.component.Jump
import com.quillraven.github.quillyjumper.component.Physic
import kotlin.math.sqrt

class JumpPhysicSystem(
    private val physicWorld: PhysicWorld = inject()
) : IteratingSystem(family { all(Jump, Physic) }) {

    override fun onTickEntity(entity: Entity) {
        val jumpCmp = entity[Jump]
        val (body, _) = entity[Physic]
        val (maxHeight, buffer) = jumpCmp

        if (buffer == 0f) {
            // entity does not want to jump (buffer=0)
            return
        }

        // TODO check if entity can jump (=entity is grounded?)
        jumpCmp.buffer = 0f
        val gravityY = if (physicWorld.gravity.y == 0f) 1f else -physicWorld.gravity.y
        body.setLinearVelocity(body.linearVelocity.x, sqrt(2 * maxHeight * gravityY))
    }

}
