package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Move
import com.quillraven.github.quillyjumper.component.Track
import com.quillraven.github.quillyjumper.system.MoveSystem.Companion.MIN_SPEED
import com.quillraven.github.quillyjumper.system.MoveSystem.Companion.MOVE_INTERPOLATION

class TrackSystem : IteratingSystem(World.family { all(Move, Track, Graphic) }) {

    override fun onTickEntity(entity: Entity) {
        val trackCmp = entity[Track]
        val (trackVertices, closedTrack, currentIdx, direction) = trackCmp
        val moveCmp = entity[Move]
        val (_, _, max, timer, timeToMax) = moveCmp
        val (sprite) = entity[Graphic]

        // adapt move speed
        moveCmp.timer = (timer + (deltaTime * (1f / timeToMax))).coerceAtMost(1f)
        moveCmp.current = MOVE_INTERPOLATION.apply(MIN_SPEED, max, moveCmp.timer)

        // move towards current track point
        val currentX = sprite.x + sprite.width * 0.5f
        val currentY = sprite.y + sprite.height * 0.5f
        if (currentIdx == -1 || trackVertices[currentIdx].inRange(currentX, currentY, 0.1f)) {
            // entity reached current track point -> go to next track point
            trackCmp.currentIdx = currentIdx + direction
            if (trackCmp.currentIdx >= trackVertices.size) {
                if (closedTrack) {
                    trackCmp.currentIdx = 0
                } else {
                    trackCmp.direction *= -1
                    trackCmp.currentIdx = trackVertices.size - 1
                }
            } else if (!closedTrack && trackCmp.currentIdx < 0) {
                trackCmp.direction *= -1
                trackCmp.currentIdx = 0
            }
            val nextTrackPoint = trackVertices[trackCmp.currentIdx]
            trackCmp.angleRad = atan2(nextTrackPoint.y - currentY, nextTrackPoint.x - currentX)
        }

        trackCmp.moveX = moveCmp.current * cos(trackCmp.angleRad)
        trackCmp.moveY = moveCmp.current * sin(trackCmp.angleRad)

        // physic body linear velocity is set inside PhysicSystem to correctly update the linear
        // velocity every time before the physic world gets updated
    }

    private fun Vector2.inRange(otherX: Float, otherY: Float, tolerance: Float): Boolean =
        isEqual(x, otherX, tolerance) && isEqual(y, otherY, tolerance)

}
