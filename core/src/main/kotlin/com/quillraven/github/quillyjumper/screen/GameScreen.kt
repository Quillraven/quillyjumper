package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.GRAVITY
import com.quillraven.github.quillyjumper.input.KeyboardInputProcessor
import com.quillraven.github.quillyjumper.system.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld

class GameScreen(batch: Batch, private val assets: Assets) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(16f, 9f, gameCamera)
    private val physicWorld = createWorld(gravity = GRAVITY).apply {
        autoClearForces = false
    }
    private val world = configureWorld {
        injectables {
            add(gameCamera)
            add("gameViewport", gameViewport)
            add(assets)
            add(batch)
            add(physicWorld)
        }

        systems {
            add(SpawnSystem())
            add(MoveSystem())
            add(JumpPhysicSystem())
            add(PhysicSystem())
            add(RenderSystem())
            add(PhysicRenderDebugSystem())
            add(GlProfilerSystem())
        }
    }
    private val keyboardProcessor = KeyboardInputProcessor(world)

    override fun show() {
        Gdx.input.inputMultiplexer.addProcessor(keyboardProcessor)
        world.systems
            .filterIsInstance<GameEventListener>()
            .forEach { GameEventDispatcher.register(it) }

        val map = assets[MapAsset.TEST]
        GameEventDispatcher.fire(MapChangeEvent(map))
    }

    override fun hide() {
        Gdx.input.inputMultiplexer.removeProcessor(keyboardProcessor)
        world.systems
            .filterIsInstance<GameEventListener>()
            .forEach { GameEventDispatcher.unregister(it) }
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
    }
}
