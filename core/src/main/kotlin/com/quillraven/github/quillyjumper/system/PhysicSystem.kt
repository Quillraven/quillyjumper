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
import com.quillraven.github.quillyjumper.event.GameEventDispatcher
import com.quillraven.github.quillyjumper.event.PlayerItemCollectEvent
import com.quillraven.github.quillyjumper.event.PlayerMapBottomContactEvent
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

        // update linear velocity if entity has a Move component
        entity.getOrNull(Move)?.let { moveCmp ->
            updateLinearVelocity(entity, moveCmp, body)
        }
    }

    private fun updateLinearVelocity(
        entity: Entity,
        moveCmp: Move,
        body: Body
    ) {
        val trackCmp = entity.getOrNull(Track)
        if (trackCmp != null) {
            // entities that follow a track have fixed velocity without any impact for gravity
            body.setLinearVelocity(trackCmp.moveX, trackCmp.moveY)
            return
        }

        val gravityScale = body.gravityScale
        when {
            // no direction specified -> stop movement
            moveCmp.direction.isNone() -> when (body.type) {
                // gravity impacts dynamic bodies -> keep current linear velocity of the y-axis
                DynamicBody -> body.setLinearVelocity(0f, body.linearVelocity.y * gravityScale)
                // other bodies are not impacted by gravity -> just stop them
                else -> body.setLinearVelocity(0f, 0f)
            }

            // horizontal movement keeps the gravity value (=linear velocity of the y-axis)
            moveCmp.direction.isLeftOrRight() -> body.setLinearVelocity(
                moveCmp.current,
                body.linearVelocity.y * gravityScale
            )

            // vertical movement is limited and does not apply a velocity on the x-axis
            else -> body.setLinearVelocity(0f, moveCmp.current)
        }

        // cap fall speed at a certain value
        if (body.linearVelocity.y < MAX_FALL_SPEED) {
            body.setLinearVelocity(body.linearVelocity.x, MAX_FALL_SPEED)
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

    override fun beginContact(contact: Contact) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB
        val entityA = contact.entityA
        val entityB = contact.entityB

        if (entityB == null && entityA != null && isPlayerMapBottomCollision(entityA, fixtureA, fixtureB)) {
            // player in contact with map bottom
            handlePlayerMapBottomBeginContact(entityA)
            return
        } else if (entityA == null && entityB != null && isPlayerMapBottomCollision(entityB, fixtureB, fixtureA)) {
            // player in contact with map bottom
            handlePlayerMapBottomBeginContact(entityB)
            return
        } else if (entityA == null || entityB == null) {
            // ignore any other collision between non entity bodies
            return
        }

        when {
            isDamageCollision(entityA, entityB, fixtureA, fixtureB) -> handleDamageBeginContact(entityA, entityB)
            isDamageCollision(entityB, entityA, fixtureB, fixtureA) -> handleDamageBeginContact(entityB, entityA)
            isAggroSensorCollision(entityA, fixtureA, fixtureB) -> handleAggroBeginContact(entityA, entityB)
            isAggroSensorCollision(entityB, fixtureB, fixtureA) -> handleAggroBeginContact(entityB, entityA)
            isCollectableCollision(entityA, entityB, fixtureA) -> handleCollectableBeginContact(entityA, entityB)
            isCollectableCollision(entityB, entityA, fixtureB) -> handleCollectableBeginContact(entityB, entityA)
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

        when {
            isDamageCollision(entityA, entityB, fixtureA, fixtureB) -> handleDamageEndContact(entityA, entityB)
            isDamageCollision(entityB, entityA, fixtureB, fixtureA) -> handleDamageEndContact(entityB, entityA)
            isAggroSensorCollision(entityA, fixtureA, fixtureB) -> handleAggroEndContact(entityA, entityB)
            isAggroSensorCollision(entityB, fixtureB, fixtureA) -> handleAggroEndContact(entityB, entityA)
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

    private fun handleAggroBeginContact(aggroEntity: Entity, triggerEntity: Entity) {
        log.debug { "Begin aggro collision between $aggroEntity and $triggerEntity" }
        aggroEntity[Aggro].aggroEntities += triggerEntity
    }

    private fun handleAggroEndContact(aggroEntity: Entity, triggerEntity: Entity) {
        log.debug { "End aggro collision between $aggroEntity and $triggerEntity" }
        val aggroCmp = aggroEntity[Aggro]
        aggroCmp.aggroEntities -= triggerEntity
        if (aggroCmp.target == triggerEntity) {
            aggroCmp.target = Entity.NONE
        }
    }

    private fun handlePlayerMapBottomBeginContact(playerEntity: Entity) {
        GameEventDispatcher.fire(PlayerMapBottomContactEvent(playerEntity))
    }

    private fun handleCollectableBeginContact(player: Entity, collectable: Entity) {
        GameEventDispatcher.fire(PlayerItemCollectEvent(player, collectable, collectable[Tiled].gameObject))
    }

    private fun isDamageCollision(entityA: Entity, entityB: Entity, fixtureA: Fixture, fixtureB: Fixture): Boolean {
        return entityA has Damage && entityB has Life && fixtureA.isHitbox() && fixtureB.isHitbox()
    }

    private fun isAggroSensorCollision(
        entityA: Entity,
        fixtureA: Fixture,
        fixtureB: Fixture
    ): Boolean {
        return entityA has Aggro && fixtureA.isAggroSensor() && fixtureB.isHitbox()
    }

    private fun isPlayerMapBottomCollision(entityA: Entity, fixtureA: Fixture, fixtureB: Fixture): Boolean {
        return entityA has EntityTag.PLAYER && fixtureA.isHitbox() && fixtureB.userData == "mapBoundaryBottom"
    }

    private fun isCollectableCollision(entityA: Entity, entityB: Entity, fixtureA: Fixture): Boolean {
        return entityA has EntityTag.PLAYER && entityB has EntityTag.COLLECTABLE && fixtureA.isHitbox()
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        contact.isEnabled = bodyCollisionEnabled(bodyA, bodyB) || bodyCollisionEnabled(bodyB, bodyA)
    }

    private fun bodyCollisionEnabled(bodyA: Body, bodyB: Body): Boolean {
        // Dynamic <-> Static collision only if the dynamic body is NOT jumping
        // -> allow player to jump through platforms
        val typeA = bodyA.type
        val typeB = bodyB.type
        return (typeA == DynamicBody && typeB == StaticBody && (bodyA.isNotJumping() || bodyB.isMapBoundary())) ||
            typeA == DynamicBody && typeB == KinematicBody ||
            typeA == DynamicBody && typeB == DynamicBody
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    companion object {
        private val log = logger<PhysicSystem>()
        private const val MAX_FALL_SPEED = -12f
    }

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

const val USER_DATA_AGGRO_SENSOR = "aggroSensor"
fun Fixture.isAggroSensor(): Boolean = isSensor && USER_DATA_AGGRO_SENSOR == userData

fun Body.isMapBoundary(): Boolean = userData == "mapBoundary"

fun Body.isNotMapBoundary(): Boolean = userData != "mapBoundary"

fun Body.isNotJumping(): Boolean = linearVelocity.y <= 3f
