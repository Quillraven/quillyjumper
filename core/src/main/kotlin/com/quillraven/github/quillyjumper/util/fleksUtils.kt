package com.quillraven.github.quillyjumper.util

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.system.AnimationSystem

fun World.entityAnimation(entity: Entity, type: AnimationType) {
    // TODO can we optimize it to retrieve the system directly without looping through all systems?
    val animationSystem = this.system<AnimationSystem>()
    animationSystem.entityAnimation(entity, type)
}
