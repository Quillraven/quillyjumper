package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.graphics.Color
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Flash(
    val color: Color,
    var weight: Float,
    var amount: Int,
    val delay: Float,
    var delayTimer: Float = delay,
    var doFlash: Boolean = true,
) : Component<Flash> {
    override fun type() = Flash

    companion object : ComponentType<Flash>()
}
