package com.quillraven.github.quillyjumper.ai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils.isEqual
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.component.Physic
import ktx.math.component1
import ktx.math.component2

private const val B2D_VEL_TOLERANCE = 0.05f
private const val B2D_VEL_JUMP_FALL = 1f

sealed interface GameObjectState : State<AiEntity> {
    override fun enter(entity: AiEntity) = Unit

    override fun update(entity: AiEntity) = Unit

    override fun exit(entity: AiEntity) = Unit

    override fun onMessage(entity: AiEntity, telegram: Telegram) = false
}

data object GameObjectStateIdle : GameObjectState {
    override fun enter(entity: AiEntity) {
        entity.animation(AnimationType.IDLE)
    }

    override fun update(entity: AiEntity) {
        val (body) = entity[Physic]
        val (linX, linY) = body.linearVelocity

        when {
            linY > B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateJump)
            linY < -B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateFall)
            !isEqual(linX, 0f, B2D_VEL_TOLERANCE) -> entity.state(GameObjectStateRun)
        }
    }
}

data object GameObjectStateJump : GameObjectState {
    override fun enter(entity: AiEntity) {
        entity.animation(AnimationType.JUMP)
    }

    override fun update(entity: AiEntity) {
        val (body) = entity[Physic]
        val (linX, linY) = body.linearVelocity

        when {
            linY < -B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateFall)
            isEqual(linY, 0f, B2D_VEL_TOLERANCE) -> {
                if (isEqual(linX, 0f, B2D_VEL_TOLERANCE)) {
                    entity.state(GameObjectStateIdle)
                } else {
                    entity.state(GameObjectStateRun)
                }
            }
        }
    }
}

data object GameObjectStateFall : GameObjectState {
    override fun enter(entity: AiEntity) {
        entity.animation(AnimationType.FALL)
    }

    override fun update(entity: AiEntity) {
        val (body) = entity[Physic]
        val (linX, linY) = body.linearVelocity

        when {
            linY > B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateJump)
            isEqual(linY, 0f, B2D_VEL_TOLERANCE) -> {
                if (isEqual(linX, 0f, B2D_VEL_TOLERANCE)) {
                    entity.state(GameObjectStateIdle)
                } else {
                    entity.state(GameObjectStateRun)
                }
            }
        }
    }
}

data object GameObjectStateRun : GameObjectState {
    override fun enter(entity: AiEntity) {
        entity.animation(AnimationType.RUN)
    }

    override fun update(entity: AiEntity) {
        val (body) = entity[Physic]
        val (linX, linY) = body.linearVelocity

        when {
            linY > B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateJump)
            linY < -B2D_VEL_JUMP_FALL -> entity.state(GameObjectStateFall)
            isEqual(linX, 0f, B2D_VEL_TOLERANCE) -> {
                entity.state(GameObjectStateIdle)
            }
        }
    }
}
