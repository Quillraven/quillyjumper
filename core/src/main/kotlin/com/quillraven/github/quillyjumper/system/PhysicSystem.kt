package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Physic
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject()
) : IteratingSystem(family = family { all(Physic, Graphic) }, interval = Fixed(1 / 45f)) {

    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            log.error { "AutoClearForces must be set to false to guarantee a correct physic step behavior." }
            physicWorld.autoClearForces = false
        }
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val (body, prevPosition) = entity[Physic]
        prevPosition.set(body.position)
    }

    // interpolate between position before world step and real position after world step for smooth rendering
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val (sprite) = entity[Graphic]
        val (body, prevPosition) = entity[Physic]

        val (prevX, prevY) = prevPosition
        val (bodyX, bodyY) = body.position
        sprite.setPosition(
            MathUtils.lerp(prevX, bodyX, alpha),
            MathUtils.lerp(prevY, bodyY, alpha),
        )
    }

    companion object {
        private val log = logger<PhysicSystem>()
    }

}
