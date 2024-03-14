package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.component.EntityTag
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.system.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.math.vec2

class GameScreen(batch: Batch, private val assets: Assets) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(10f, 7f, gameCamera)
    private val physicWorld = createWorld(gravity = earthGravity).apply {
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
            add(PhysicSystem())
            add(RenderSystem())
            add(PhysicRenderDebugSystem())
            add(GlProfilerSystem())
        }
    }

    override fun show() {
        world.systems
            .filterIsInstance<GameEventListener>()
            .forEach { GameEventDispatcher.register(it) }

        val map = assets[MapAsset.TEST]
        GameEventDispatcher.fire(MapChangeEvent(map))
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        // TODO remove debug controls
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.A) -> {
                world.family { all(EntityTag.PLAYER) }.forEach { entity ->
                    val (body) = entity[Physic]
                    body.applyForce(vec2(-100f, 0f), body.worldCenter, true)
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.D) -> {
                world.family { all(EntityTag.PLAYER) }.forEach { entity ->
                    val (body) = entity[Physic]
                    body.applyForce(vec2(100f, 0f), body.worldCenter, true)
                }
            }
        }

        world.update(delta)
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
    }
}
