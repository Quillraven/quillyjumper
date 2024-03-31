package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity

data class Aggro(
    val aggroEntities: MutableList<Entity> = mutableListOf(),
    var target: Entity = Entity.NONE,
    val sourceLocation: Vector2,
    val range: Float,
) : Component<Aggro> {
    override fun type() = Aggro

    companion object : ComponentType<Aggro>()
}
