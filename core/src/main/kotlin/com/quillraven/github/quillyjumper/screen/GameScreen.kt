package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameProperties
import com.quillraven.github.quillyjumper.MapAsset
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.GRAVITY
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.component.EntityTag
import com.quillraven.github.quillyjumper.component.Life
import com.quillraven.github.quillyjumper.event.EntityDamageEvent
import com.quillraven.github.quillyjumper.event.GameEventDispatcher
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import com.quillraven.github.quillyjumper.input.KeyboardInputProcessor
import com.quillraven.github.quillyjumper.inputMultiplexer
import com.quillraven.github.quillyjumper.system.*
import com.quillraven.github.quillyjumper.tiled.TiledService
import com.quillraven.github.quillyjumper.ui.GameModel
import com.quillraven.github.quillyjumper.ui.gameView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.scene2d.actors

class GameScreen(
    batch: Batch,
    private val assets: Assets,
    gameProperties: GameProperties,
    audioService: AudioService
) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(16f, 9f, gameCamera)
    private val uiViewport: Viewport = FitViewport(320f, 180f)
    private val stage = Stage(uiViewport, batch)
    private val physicWorld = createWorld(gravity = GRAVITY).apply { autoClearForces = false }
    private val world = createEntityWorld(batch, audioService, gameProperties)
    private val tiledService = TiledService(world, physicWorld, assets)
    private val keyboardProcessor = KeyboardInputProcessor(world)
    private val gameModel = GameModel(world)

    override fun show() {
        Gdx.input.inputMultiplexer.addProcessor(keyboardProcessor)
        Gdx.input.inputMultiplexer.addProcessor(stage)

        GameEventDispatcher.register(tiledService)
        GameEventDispatcher.register(gameModel)
        world.systems
            .filterIsInstance<GameEventListener>()
            .forEach { GameEventDispatcher.register(it) }
        physicWorld.setContactListener(world.system<PhysicSystem>())

        // setup UI
        stage.actors {
            gameView(gameModel)
        }

        // set first map
        val map = assets[MapAsset.TEST]
        GameEventDispatcher.fire(MapChangeEvent(map))
    }

    override fun hide() {
        Gdx.input.inputMultiplexer.clear()
        GameEventDispatcher.unregister(gameModel)
        GameEventDispatcher.unregister(tiledService)
        world.systems
            .filterIsInstance<GameEventListener>()
            .forEach { GameEventDispatcher.unregister(it) }

        // clear UI stage
        stage.clear()
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        // TODO remove debug stuff
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) -> {
                world.family { all(EntityTag.PLAYER) }.forEach {
                    it[Life].current = 0f
                    GameEventDispatcher.fire(EntityDamageEvent(it, it[Life]))
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) -> {
                world.family { all(EntityTag.PLAYER) }.forEach {
                    it[Life].current = 1f
                    GameEventDispatcher.fire(EntityDamageEvent(it, it[Life]))
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) -> {
                world.family { all(EntityTag.PLAYER) }.forEach {
                    it[Life].current = 2f
                    GameEventDispatcher.fire(EntityDamageEvent(it, it[Life]))
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) -> {
                world.family { all(EntityTag.PLAYER) }.forEach {
                    it[Life].current = 3f
                    GameEventDispatcher.fire(EntityDamageEvent(it, it[Life]))
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) -> {
                world.family { all(EntityTag.PLAYER) }.forEach {
                    it[Life].current = 4f
                    GameEventDispatcher.fire(EntityDamageEvent(it, it[Life]))
                }
            }
        }

        world.update(delta)
    }

    private fun createEntityWorld(
        batch: Batch,
        audioService: AudioService,
        gameProperties: GameProperties
    ) = configureWorld {
        injectables {
            add(gameCamera)
            add("gameViewport", gameViewport)
            add("uiViewport", uiViewport)
            add(stage)
            add(assets)
            add(batch)
            add(physicWorld)
            add(audioService)
        }

        systems {
            add(MoveSystem())
            add(TrackSystem())
            add(JumpPhysicSystem())
            add(PhysicSystem())
            add(StateSystem())
            add(AnimationSystem())
            add(CameraSystem())
            add(RenderSystem())
            if (gameProperties.debugPhysic) {
                add(PhysicRenderDebugSystem())
            }
            if (gameProperties.enableProfiling) {
                add(GlProfilerSystem())
            }
            if (gameProperties.debugTrack) {
                add(TrackDebugSystem())
            }
        }
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
        stage.disposeSafely()
    }
}
