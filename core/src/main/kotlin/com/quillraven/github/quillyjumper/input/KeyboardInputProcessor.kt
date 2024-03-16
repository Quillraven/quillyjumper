package com.quillraven.github.quillyjumper.input

import com.badlogic.gdx.Input.Keys
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.EntityTag
import com.quillraven.github.quillyjumper.component.Jump
import com.quillraven.github.quillyjumper.component.Jump.Companion.JUMP_BUFFER_TIME
import com.quillraven.github.quillyjumper.component.Move
import com.quillraven.github.quillyjumper.component.MoveDirection
import ktx.app.KtxInputAdapter

class KeyboardInputProcessor(world: World) : KtxInputAdapter {

    private var moveX = 0
    private val playerEntities = world.family { all(EntityTag.PLAYER) }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Keys.D -> updatePlayerMovement(1)
            Keys.A -> updatePlayerMovement(-1)
            Keys.SPACE -> playerEntities.forEach { it[Jump].buffer = JUMP_BUFFER_TIME }
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Keys.D -> updatePlayerMovement(-1)
            Keys.A -> updatePlayerMovement(1)
        }

        return false
    }

    private fun updatePlayerMovement(moveValue: Int) {
        moveX += moveValue
        playerEntities.forEach { it[Move].direction = MoveDirection.of(moveX) }
    }

}
