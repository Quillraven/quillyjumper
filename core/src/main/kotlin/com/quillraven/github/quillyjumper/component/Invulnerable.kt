package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Invulnerable(var time: Float) : Component<Invulnerable> {
    override fun type() = Invulnerable

    companion object : ComponentType<Invulnerable>()
}
