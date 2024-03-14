package com.quillraven.github.quillyjumper

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.quillraven.github.quillyjumper.screen.GameScreen
import com.quillraven.github.quillyjumper.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

typealias PhysicWorld = World

class Quillyjumper : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets: Assets by lazy { Assets() }

    override fun create() {
        addScreen(LoadingScreen(this, assets))
        addScreen(GameScreen(batch, assets))
        setScreen<LoadingScreen>()
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.dispose()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f // 16 pixels == 1 meter in Box2D
        val OBJECT_FIXTURES = mutableMapOf<GameObject, List<FixtureDef>>()
    }
}
