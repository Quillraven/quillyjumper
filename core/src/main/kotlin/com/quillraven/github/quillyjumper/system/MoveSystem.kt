package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.Interpolation
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.GameObject.FINISH_FLAG
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.PlayerItemCollectEvent

class MoveSystem : IteratingSystem(family { all(Move).none(Track) }), GameEventListener {

    override fun onTickEntity(entity: Entity) {
        val moveCmp = entity[Move]
        val (direction, current, max, timer, timeToMax) = moveCmp

        if (direction == MoveDirection.NONE) {
            // entity wants to stop movement (=direction == NONE)
            moveCmp.current = 0f
            moveCmp.timer = 0f
            return
        }

        // entity wants to move right/up or left/down
        if ((current > 0 && direction.isLeftOrDown()) || (current < 0 && direction.isRightOrUp())) {
            // entity wants to change direction -> reset timer to start from slow move speed again
            moveCmp.current = 0f
            moveCmp.timer = 0f
            return
        }

        // flip the sprite to look to the left, when the entity wants to move to the left
        entity.getOrNull(Graphic)?.let {
            it.sprite.setFlip(direction == MoveDirection.LEFT, it.sprite.isFlipY)
        }

        moveCmp.timer = (timer + (deltaTime * (1f / timeToMax))).coerceAtMost(1f)
        if (direction.isLeftOrRight()) {
            moveCmp.current = MOVE_INTERPOLATION.apply(MIN_SPEED, max, moveCmp.timer) * direction.valueX
        } else {
            moveCmp.current = MOVE_INTERPOLATION.apply(MIN_SPEED, max, moveCmp.timer) * direction.valueY
        }
    }

    override fun onEvent(event: GameEvent) {
        if (event is PlayerItemCollectEvent && event.collectableType == FINISH_FLAG) {
            event.player.configure {
                // this disables the PlayerInputController from activating the movement again
                it -= EntityTag.PLAYER
                // move player to the right and slow him down over time (=liner damping)
                val body = it[Physic].body
                body.setLinearVelocity(it[Move].max, body.linearVelocity.y)
                body.linearDamping = 2.5f
                // remove Move component to not update linear velocity in PhysicSystem
                it -= Move
            }
        }
    }

    companion object {
        const val MIN_SPEED = 1f
        val MOVE_INTERPOLATION: Interpolation = Interpolation.pow5Out
    }

}
