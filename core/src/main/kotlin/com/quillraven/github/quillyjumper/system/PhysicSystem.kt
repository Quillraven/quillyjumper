package com.quillraven.github.quillyjumper.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.component.Physic
import ktx.log.logger

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject()
) : IteratingSystem(family = family { all(Physic) }, interval = Fixed(1 / 45f)) {

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
        val (body) = entity[Physic]
        // physicCmp.prevPos.set(physicCmp.body.position)

//        if (!physicCmp.impulse.isZero) {
            //body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
//            physicCmp.impulse.setZero()
//        }
    }

    // interpolate between position before world step and real position after world step for smooth rendering
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        //val imageCmp = imageCmps[entity]
        val (body) = entity[Physic]

//        imageCmp.image.run {
//            val (prevX, prevY) = physicCmp.prevPos
//            val (bodyX, bodyY) = physicCmp.body.position
//
//            setPosition(
//                MathUtils.lerp(prevX, bodyX, alpha) - width * 0.5f,
//                MathUtils.lerp(prevY, bodyY, alpha) - height * 0.5f
//            )
//        }
    }

    companion object {
        private val log = logger<PhysicSystem>()
    }

}
