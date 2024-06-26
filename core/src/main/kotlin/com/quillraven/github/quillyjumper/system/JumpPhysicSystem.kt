package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.MathUtils.isEqual
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.SoundAsset
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.component.Jump
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.PlayerItemCollectEvent
import ktx.box2d.query
import kotlin.math.sqrt

class JumpPhysicSystem(
    private val physicWorld: PhysicWorld = inject(),
    private val audioService: AudioService = inject(),
) : IteratingSystem(family { all(Jump, Physic) }), GameEventListener {

    override fun onTickEntity(entity: Entity) {
        val jumpCmp = entity[Jump]
        val (body, _) = entity[Physic]
        val (maxHeight, lowerFeet, upperFeet, buffer, airJump) = jumpCmp

        if (airJump) {
            // collected fruit to do a mid-air jump
            jumpCmp.airJump = false
            applyJumpVelocity(jumpCmp, body, maxHeight * 0.75f)
            return
        }

        if (buffer == 0f) {
            // entity does not want to jump (buffer=0)
            return
        }

        jumpCmp.buffer = (jumpCmp.buffer - deltaTime).coerceAtLeast(0f)
        if (!isEqual(body.linearVelocity.y, 0f, 2f)) {
            // entity is already jumping or falling -> do not jump
            // falling velocity.y is ~ -1.5 when just walking off a cliff
            return
        }

        // check if entity is grounded via AABB query
        val lowerX = body.position.x + lowerFeet.x - FEET_TOLERANCE
        val lowerY = body.position.y + lowerFeet.y - FEET_TOLERANCE
        val upperX = body.position.x + upperFeet.x + FEET_TOLERANCE
        val upperY = body.position.y + upperFeet.y + FEET_TOLERANCE
        physicWorld.query(lowerX, lowerY, upperX, upperY) { fixture ->
            if (fixture.body != body && fixture.body.isNotMapBoundary()) {
                // entity is in contact with something -> jump
                applyJumpVelocity(jumpCmp, body, maxHeight)
                return@query false
            }

            return@query true
        }
    }

    private fun applyJumpVelocity(
        jumpCmp: Jump,
        body: Body,
        maxHeight: Float
    ) {
        jumpCmp.buffer = 0f
        val gravityY = if (physicWorld.gravity.y == 0f) 1f else -physicWorld.gravity.y
        body.setLinearVelocity(body.linearVelocity.x, sqrt(2 * maxHeight * gravityY))
        audioService.play(SoundAsset.JUMP)
    }

    fun debugFeetAABB(entity: Entity, resultRect: Rectangle) {
        val jumpCmp = entity[Jump]
        val (body, _) = entity[Physic]
        val (_, lowerFeet, upperFeet) = jumpCmp

        val lowerX = body.position.x + lowerFeet.x - FEET_TOLERANCE
        val lowerY = body.position.y + lowerFeet.y - FEET_TOLERANCE
        val upperX = body.position.x + upperFeet.x + FEET_TOLERANCE
        val upperY = body.position.y + upperFeet.y + FEET_TOLERANCE
        resultRect.set(lowerX, lowerY, upperX - lowerX, upperY - lowerY)
    }

    override fun onEvent(event: GameEvent) {
        if (event is PlayerItemCollectEvent && event.collectableType == GameObject.CHERRY) {
            val jumpCmp = event.player[Jump]
            val (body) = event.player[Physic]
            applyJumpVelocity(jumpCmp, body, jumpCmp.maxHeight * 0.75f)
        }
    }

    companion object {
        // the original feet fixture is extended a little bit to allow players to jump also
        // when they just went off a cliff for example (=little tolerance for better controls)
        private const val FEET_TOLERANCE = 0.3f
    }

}
