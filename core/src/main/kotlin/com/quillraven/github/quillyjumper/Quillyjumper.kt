package com.quillraven.github.quillyjumper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.PropertiesUtils
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.screen.LoadingScreen
import com.quillraven.github.quillyjumper.util.FixtureDefUserData
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.math.vec2

typealias PhysicWorld = World

val Input.inputMultiplexer: InputMultiplexer
    get() = this.inputProcessor as InputMultiplexer

class Quillyjumper : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets: Assets by lazy { Assets() }
    private val gameProperties: GameProperties by lazy(::loadGameProperties)
    private val audioService: AudioService by lazy {
        AudioService(assets, soundVolume = gameProperties.soundVolume, musicVolume = gameProperties.musicVolume)
    }

    override fun create() {
        Gdx.app.logLevel = gameProperties.logLevel
        log.debug { "Log level is ${Gdx.app.logLevel}" }

        Gdx.input.inputProcessor = InputMultiplexer()

        addScreen(LoadingScreen(this, batch, assets, gameProperties, audioService))
        setScreen<LoadingScreen>()
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        currentScreen.render(Gdx.graphics.deltaTime.coerceAtMost(0.25f))
        audioService.update()
    }

    private fun loadGameProperties(): GameProperties {
        val propertiesMap = ObjectMap<String, String>()
        Gdx.files.internal("game.properties").reader().use {
            PropertiesUtils.load(propertiesMap, it)
        }
        return propertiesMap.toGameProperties()
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.disposeSafely()
    }

    companion object {
        private val log = logger<Quillyjumper>()
        const val UNIT_SCALE = 1 / 16f // 16 pixels == 1 meter in Box2D
        val GRAVITY = vec2(0f, -30f)
        val OBJECT_FIXTURES = mutableMapOf<GameObject, List<FixtureDefUserData>>()
    }
}
