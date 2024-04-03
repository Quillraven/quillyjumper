package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.math.vec2

data class Physic(
    var body: Body, // var instead of val because body teleportation requires recreation of body
    // position of the physic body since the last physic's world step call
    val prevPosition: Vector2 = vec2(),
) : Component<Physic> {

    override fun type() = Physic

    companion object : ComponentType<Physic>()
}
