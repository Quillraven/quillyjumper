package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

enum class MoveDirection(val valueX: Int, val valueY: Int) {
    LEFT(-1, 0),
    NONE(0, 0),
    RIGHT(1, 0),
    DOWN(0, -1),
    UP(0, 1);

    fun opposite(): MoveDirection = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        DOWN -> UP
        UP -> DOWN
        NONE -> NONE
    }

    fun isLeftOrDown() = this == LEFT || this == DOWN

    fun isRightOrUp() = this == RIGHT || this == UP

    fun isNone() = this == NONE

    fun isLeftOrRight() = this == RIGHT || this == LEFT

    companion object {
        fun ofValueX(value: Int): MoveDirection = when (value) {
            1 -> RIGHT
            -1 -> LEFT
            else -> NONE
        }
    }
}

data class Move(
    var direction: MoveDirection = MoveDirection.NONE,
    var current: Float = 0f,
    var max: Float,
    var timer: Float = 0f,
    var timeToMax: Float,
) : Component<Move> {
    override fun type() = Move

    companion object : ComponentType<Move>()
}
