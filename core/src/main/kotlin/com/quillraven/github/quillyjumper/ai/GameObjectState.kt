package com.quillraven.github.quillyjumper.ai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.MathUtils.PI
import com.badlogic.gdx.math.MathUtils.isEqual
import com.github.quillraven.fleks.Entity
import com.quillraven.github.quillyjumper.component.*
import ktx.math.component1
import ktx.math.component2

enum class GameObjectState : State<AiEntity> {
    IDLE {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: AiEntity) {
            val (body) = entity[Physic]
            val (linX, linY) = body.linearVelocity

            when {
                linY > B2D_VEL_JUMP_FALL -> entity.state(JUMP)
                linY < -B2D_VEL_JUMP_FALL -> entity.state(FALL)
                !isEqual(linX, 0f, B2D_VEL_TOLERANCE) -> entity.state(RUN)
            }
        }
    },

    JUMP {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.JUMP)
        }

        override fun update(entity: AiEntity) {
            val (body) = entity[Physic]
            val (linX, linY) = body.linearVelocity

            when {
                linY < -B2D_VEL_JUMP_FALL -> entity.state(FALL)
                isEqual(linY, 0f, B2D_VEL_TOLERANCE) -> {
                    if (isEqual(linX, 0f, B2D_VEL_TOLERANCE)) {
                        entity.state(IDLE)
                    } else {
                        entity.state(RUN)
                    }
                }
            }
        }
    },

    FALL {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.FALL)
        }

        override fun update(entity: AiEntity) {
            val (body) = entity[Physic]
            val (linX, linY) = body.linearVelocity

            when {
                linY > B2D_VEL_JUMP_FALL -> entity.state(JUMP)
                isEqual(linY, 0f, B2D_VEL_TOLERANCE) -> {
                    if (isEqual(linX, 0f, B2D_VEL_TOLERANCE)) {
                        entity.state(IDLE)
                    } else {
                        entity.state(RUN)
                    }
                }
            }
        }
    },

    RUN {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: AiEntity) {
            val (body) = entity[Physic]
            val (linX, linY) = body.linearVelocity

            when {
                linY > B2D_VEL_JUMP_FALL -> entity.state(JUMP)
                linY < -B2D_VEL_JUMP_FALL -> entity.state(FALL)
                isEqual(linX, 0f, B2D_VEL_TOLERANCE) -> {
                    entity.state(IDLE)
                }
            }
        }
    },

    ROCK_HEAD_IDLE {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: AiEntity) {
            entity.aggroTarget()?.let { aggroTarget ->
                if (entity.isPathBlocked(aggroTarget)) {
                    // cannot move to target location -> ignore the target
                    return
                }

                entity[Aggro].target = aggroTarget
                entity.state(ROCK_HEAD_AGGRO)
            }
        }
    },

    ROCK_HEAD_AGGRO {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.AGGRO, PlayMode.NORMAL)
        }

        override fun update(entity: AiEntity) {
            val (_, targetEntity, _, range) = entity[Aggro]
            if (targetEntity == Entity.NONE) {
                entity.state(ROCK_HEAD_IDLE)
            } else if (entity.inRange(targetEntity, range * 0.9f)) {
                entity.state(ROCK_HEAD_ATTACK)
            }
        }
    },

    ROCK_HEAD_ATTACK {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)

            // move rock head towards target entity location
            val (_, targetEntity) = entity[Aggro]
            val angle = entity.angleTo(targetEntity)
            when {
                angle <= QUARTER_PI && angle >= -QUARTER_PI -> entity[Move].direction = MoveDirection.RIGHT
                angle <= -QUARTER_PI && angle >= -THREE_QUARTER_PI -> entity[Move].direction = MoveDirection.DOWN
                angle >= THREE_QUARTER_PI || angle <= -THREE_QUARTER_PI -> entity[Move].direction = MoveDirection.LEFT
                else -> entity[Move].direction = MoveDirection.UP
            }
        }

        override fun update(entity: AiEntity) {
            val (_, _, sourceLocation, range) = entity[Aggro]
            val (body) = entity[Physic]
            val (linX, linY) = body.linearVelocity
            val notMoving = isEqual(linX, 0f, 0.01f) && isEqual(linY, 0f, 0.01f)

            if (entity.notInRange(sourceLocation, range) || notMoving) {
                entity.state(ROCK_HEAD_RETURN)
            }
        }
    },

    ROCK_HEAD_RETURN {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE)
            val moveCmp = entity[Move]
            moveCmp.direction = moveCmp.direction.opposite()
        }

        override fun update(entity: AiEntity) {
            val (_, _, sourceLocation) = entity[Aggro]

            if (entity.inRange(sourceLocation, 0.2f)) {
                entity[Move].direction = MoveDirection.NONE
                entity.state(ROCK_HEAD_IDLE)
            }
        }
    };

    override fun enter(entity: AiEntity) = Unit

    override fun update(entity: AiEntity) = Unit

    override fun exit(entity: AiEntity) = Unit

    override fun onMessage(entity: AiEntity, telegram: Telegram) = false

    companion object {
        private const val QUARTER_PI = PI * 0.25f
        private const val THREE_QUARTER_PI = PI * 0.75f
        private const val B2D_VEL_TOLERANCE = 0.05f
        private const val B2D_VEL_JUMP_FALL = 1f
    }
}
