package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Jump(
    var maxHeight: Float,
    val lowerFeet: Vector2,
    val upperFeet: Vector2,
    var buffer: Float = 0f
) : Component<Jump> {
    override fun type() = Jump

    companion object : ComponentType<Jump>() {
        const val JUMP_BUFFER_TIME = 0.2f
    }
}
