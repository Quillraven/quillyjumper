package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.math.vec2

data class Graphic(val sprite: Sprite, val z: Int) : Component<Graphic>, Comparable<Graphic> {
    val center = vec2()
        get() {
            field.x = sprite.x + sprite.width * 0.5f
            field.y = sprite.y + sprite.height * 0.5f
            return field
        }

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
