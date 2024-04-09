package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.component.Remove

class RemoveSystem : IteratingSystem(family { all(Remove) }) {

    override fun onTickEntity(entity: Entity) {
        val removeCmp = entity[Remove]

        if (removeCmp.removePhysic && entity has Physic) {
            removeCmp.removePhysic = false
            entity.configure { it -= Physic }
        }

        removeCmp.time -= deltaTime
        if (removeCmp.time <= 0.0) {
            entity.remove()
        }
    }

}
