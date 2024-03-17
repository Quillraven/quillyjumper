package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.State

class StateSystem : IteratingSystem(family { all(State) }) {

    override fun onTickEntity(entity: Entity) {
        entity[State].fsm.update()
    }

}
