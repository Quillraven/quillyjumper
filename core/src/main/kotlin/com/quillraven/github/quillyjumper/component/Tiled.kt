package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Rectangle
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.quillraven.github.quillyjumper.GameObject

data class Tiled(
    val gameObject: GameObject,
    val mapObjectID: Int,
    val mapObjectBoundary: Rectangle, // units are in Tiled units (=pixels instead of world units)
) : Component<Tiled> {
    override fun type() = Tiled

    companion object : ComponentType<Tiled>()
}
