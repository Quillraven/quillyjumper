package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Life(var current: Float, var max: Float) : Component<Life> {

    constructor(max: Int) : this(max.toFloat(), max.toFloat())

    override fun type() = Life

    companion object : ComponentType<Life>()
}
