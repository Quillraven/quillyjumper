package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.Interpolation
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.quillraven.github.quillyjumper.component.Move
import com.quillraven.github.quillyjumper.component.MoveDirection

class MoveSystem : IteratingSystem(family { all(Move) }) {

    override fun onTickEntity(entity: Entity) {
        val moveCmp = entity[Move]
        var (direction, current, max, timer, timeToMax) = moveCmp

        if (direction != MoveDirection.NONE) {
            // entity wants to move right or left
            if ((current > 0 && direction == MoveDirection.LEFT) || (current < 0 && direction == MoveDirection.RIGHT)) {
                // entity wants to change direction -> reset timer to start from slow move speed again
                timer = 0f
            }
            timer = (timer + (deltaTime * (1f / timeToMax))).coerceAtMost(1f)
            current = Interpolation.pow5Out.apply(0f, max, timer)
            current *= direction.value
        } else {
            // entity wants to stop movement
            current = 0f
            timer = 0f
        }

        moveCmp.current = current
        moveCmp.timer = timer
    }

}
