package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.component.Teleport
import com.quillraven.github.quillyjumper.component.Tiled
import ktx.box2d.body

class TeleportSystem : IteratingSystem(family { all(Teleport, Physic, Tiled) }) {

    override fun onTickEntity(entity: Entity) {
        val physicCmp = entity[Physic]
        val (oldBody) = physicCmp
        val (to) = entity[Teleport]

        // teleport physic body to new location by destroying it and recreating it at the new location
        physicCmp.body = oldBody.cpy(to)
        oldBody.world.destroyBody(oldBody)

        // teleportation is done -> remove component
        entity.configure { it -= Teleport }
    }

    private fun Body.cpy(newPosition: Vector2): Body {
        val origBody = this@cpy
        val physicWorld = origBody.world

        val newBody = physicWorld.body(origBody.type) {
            position.set(newPosition.x, newPosition.y)
            fixedRotation = origBody.isFixedRotation
            userData = origBody.userData
        }

        for (oldFixture in origBody.fixtureList) {
            newBody.createFixture(oldFixture.shape, oldFixture.density).run {
                userData = oldFixture.userData
                isSensor = oldFixture.isSensor
                friction = oldFixture.friction
                restitution = oldFixture.restitution
            }
        }

        return newBody
    }

}
