package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.Interval
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.component.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject(),
    interval: Interval = Fixed(1 / 60f),
) : IteratingSystem(family = family { all(Physic).any(Move, Graphic) }, interval = interval), ContactListener {

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

        // update linear velocity.x if entity has a Move component
        entity.getOrNull(Move)?.let { moveCmp ->
            val trackCmp = entity.getOrNull(Track)
            if (trackCmp == null) {
                body.setLinearVelocity(moveCmp.current, body.linearVelocity.y)
            } else {
                body.setLinearVelocity(trackCmp.moveX, trackCmp.moveY)
            }
        }
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

    private val Fixture.entity: Entity?
        get() {
            val userData = this.body.userData
            if (userData is Entity) {
                return userData
            }
            return null
        }

    private val Contact.entityA: Entity?
        get() = fixtureA.entity

    private val Contact.entityB: Entity?
        get() = fixtureB.entity

    private fun Fixture.isHitbox(): Boolean = "hitbox" == userData

    override fun beginContact(contact: Contact) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB
        val entityA = contact.entityA
        val entityB = contact.entityB
        if (entityA == null || entityB == null) {
            // ignore collision between non entity bodies
            return
        }

        if (isDamageCollision(entityA, entityB, fixtureA, fixtureB)) {
            handleDamageBeginContact(entityA, entityB)
        } else if (isDamageCollision(entityB, entityA, fixtureB, fixtureA)) {
            handleDamageBeginContact(entityB, entityA)
        }
    }

    override fun endContact(contact: Contact) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB
        val entityA = contact.entityA
        val entityB = contact.entityB
        if (entityA == null || entityB == null) {
            // ignore collision between non entity bodies
            return
        }

        if (isDamageCollision(entityA, entityB, fixtureA, fixtureB)) {
            handleDamageEndContact(entityA, entityB)
        } else if (isDamageCollision(entityB, entityA, fixtureB, fixtureA)) {
            handleDamageEndContact(entityB, entityA)
        }
    }

    // update target's DamageTaken component amount. This will also add the component, if it is not added yet
    private fun handleDamageBeginContact(damageSource: Entity, damageTarget: Entity) = with(world) {
        log.debug { "Begin Damage collision between $damageSource and $damageTarget" }

        val (damageAmount) = damageSource[Damage]
        damageTarget.configure {
            val damageTakenCmp = it.getOrAdd(DamageTaken) { DamageTaken(0) }
            damageTakenCmp.amount += damageAmount
        }
    }

    // remove damage from target and if there is no damage left then remove DamageTaken component
    private fun handleDamageEndContact(damageSource: Entity, damageTarget: Entity) = with(world) {
        log.debug { "End Damage collision between $damageSource and $damageTarget" }

        damageTarget.getOrNull(DamageTaken)?.let { damageTakenCmp ->
            val (damageAmount) = damageSource[Damage]
            damageTakenCmp.amount -= damageAmount
            if (damageTakenCmp.amount <= 0) {
                damageTarget.configure { it -= DamageTaken }
            }
        }
    }

    private fun isDamageCollision(entityA: Entity, entityB: Entity, fixtureA: Fixture, fixtureB: Fixture): Boolean {
        return entityA has Damage && entityB has Life && fixtureA.isHitbox() && fixtureB.isHitbox()
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        contact.isEnabled = bodyCollisionEnabled(bodyA, bodyB) || bodyCollisionEnabled(bodyB, bodyA)
    }

    private fun bodyCollisionEnabled(bodyA: Body, bodyB: Body): Boolean {
        return bodyA.type == DynamicBody && bodyB.type == StaticBody ||
            bodyA.type == DynamicBody && bodyB.type == KinematicBody ||
            bodyA.type == DynamicBody && bodyB.type == DynamicBody
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    companion object {
        private val log = logger<PhysicSystem>()
    }

}
