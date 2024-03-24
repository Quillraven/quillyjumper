package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.Invulnerable

class InvulnerableSystem : IteratingSystem(World.family { all(Invulnerable) }) {

    override fun onTickEntity(entity: Entity) {
        val invuCmp = entity[Invulnerable]
        invuCmp.time -= deltaTime
        if (invuCmp.time <= 0f) {
            entity.configure { it -= Invulnerable }
        }
    }

}
