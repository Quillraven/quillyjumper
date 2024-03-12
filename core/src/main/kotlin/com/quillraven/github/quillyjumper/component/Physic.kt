package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Physic(val body: Body) : Component<Physic> {
    override fun type() = Physic

    companion object : ComponentType<Physic>()
}
