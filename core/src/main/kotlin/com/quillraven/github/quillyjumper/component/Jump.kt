package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Jump(var maxHeight: Float, var buffer: Float = 0f) : Component<Jump> {
    override fun type() = Jump

    companion object : ComponentType<Jump>() {
        const val JUMP_BUFFER_TIME = 0.25f
    }
}
