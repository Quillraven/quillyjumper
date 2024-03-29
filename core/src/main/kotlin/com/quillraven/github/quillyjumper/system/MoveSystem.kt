package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.Interpolation
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Move
import com.quillraven.github.quillyjumper.component.MoveDirection
import com.quillraven.github.quillyjumper.component.Track

class MoveSystem : IteratingSystem(family { all(Move).none(Track) }) {

    override fun onTickEntity(entity: Entity) {
        val moveCmp = entity[Move]
        val (direction, current, max, timer, timeToMax) = moveCmp

        if (direction != MoveDirection.NONE) {
            // entity wants to move right or left
            if ((current > 0 && direction == MoveDirection.LEFT) || (current < 0 && direction == MoveDirection.RIGHT)) {
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
            moveCmp.current = MOVE_INTERPOLATION.apply(MIN_SPEED, max, moveCmp.timer) * direction.value
            return
        }

        // entity wants to stop movement (=direction == NONE)
        moveCmp.current = 0f
        moveCmp.timer = 0f
    }

    companion object {
        const val MIN_SPEED = 1f
        val MOVE_INTERPOLATION = Interpolation.pow5Out
    }

}
