package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Teleport(val to: Vector2) : Component<Teleport> {
    override fun type() = Teleport

    companion object : ComponentType<Teleport>()
}
