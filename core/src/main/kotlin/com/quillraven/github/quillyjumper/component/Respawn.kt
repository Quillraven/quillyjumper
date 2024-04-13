package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.World

typealias RespawnAction = World.() -> Unit

data class Respawn(var time: Float, var action: RespawnAction? = null) : Component<Respawn> {
    override fun type() = Respawn

    companion object : ComponentType<Respawn>()
}
