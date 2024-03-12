package com.quillraven.github.quillyjumper

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.quillraven.github.quillyjumper.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

typealias PhysicWorld = World

class Quillyjumper : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets: Assets by lazy { Assets() }

    override fun create() {
        addScreen(GameScreen(batch, assets))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.dispose()
    }

    companion object {
        val UNIT_SCALE = 1 / 16f // 16 pixels == 1 meter in Box2D
    }
}
