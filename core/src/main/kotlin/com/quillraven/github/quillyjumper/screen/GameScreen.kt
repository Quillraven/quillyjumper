package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.system.RenderSystem
import ktx.app.KtxScreen

class GameScreen(batch: Batch, private val assets: Assets) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(10f, 7f, gameCamera)
    private val world = configureWorld {
        injectables {
            add(gameCamera)
            add("gameViewport", gameViewport)
            add(assets)
            add(batch)
        }

        systems {
            add(RenderSystem())
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
        world.update(delta)
    }

    override fun dispose() {
        world.dispose()
    }
}
