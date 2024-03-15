package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Graphic(val sprite: Sprite, val z: Int = 0) : Component<Graphic>, Comparable<Graphic> {
    override fun type() = Graphic

    override fun compareTo(other: Graphic): Int = when {
        z < other.z -> -1
        z > other.z -> 1
        sprite.y < other.sprite.y -> -1
        sprite.y > other.sprite.y -> 1
        sprite.x < other.sprite.x -> -1
        sprite.x > other.sprite.x -> 1
        else -> 0
    }

    companion object : ComponentType<Graphic>()
}
