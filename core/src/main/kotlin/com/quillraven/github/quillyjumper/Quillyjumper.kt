package com.quillraven.github.quillyjumper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.quillraven.github.quillyjumper.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.math.vec2

typealias PhysicWorld = World

val Input.inputMultiplexer: InputMultiplexer
    get() = this.inputProcessor as InputMultiplexer

class Quillyjumper : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets: Assets by lazy { Assets() }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer()

        addScreen(LoadingScreen(this, batch, assets))
        setScreen<LoadingScreen>()
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        currentScreen.render(Gdx.graphics.deltaTime.coerceAtMost(0.25f))
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.disposeSafely()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f // 16 pixels == 1 meter in Box2D
        val GRAVITY = vec2(0f, -30f)
        val OBJECT_FIXTURES = mutableMapOf<GameObject, List<FixtureDef>>()
    }
}
