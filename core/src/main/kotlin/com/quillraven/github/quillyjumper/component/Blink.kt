package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Blink(
    var maxTime: Float,
    val blinkRatio: Float, // amount in seconds after a blink is happening
    var timer: Float = 0f,
) : Component<Blink> {
    override fun type() = Blink

    companion object : ComponentType<Blink>()
}
