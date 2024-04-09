package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Remove(
    var time: Float,
    var removePhysic: Boolean,
) : Component<Remove> {
    override fun type() = Remove

    companion object : ComponentType<Remove>()
}
