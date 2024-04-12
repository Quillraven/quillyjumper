package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.quillraven.github.quillyjumper.GamePreferences
import com.quillraven.github.quillyjumper.Quillyjumper
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.inputMultiplexer
import com.quillraven.github.quillyjumper.ui.MenuModel
import com.quillraven.github.quillyjumper.ui.menuView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.actors

class MenuScreen(
    batch: Batch,
    private val audioService: AudioService,
    private val game: Quillyjumper,
    private val prefs: GamePreferences,
) : KtxScreen {

    private val uiViewport: Viewport = FitViewport(320f, 180f)
    private val stage = Stage(uiViewport, batch)

    override fun show() {
        Gdx.input.inputMultiplexer.addProcessor(stage)

        stage.actors { menuView(MenuModel(game, prefs)) }
    }

    override fun resize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
    }

    override fun hide() {
        Gdx.input.inputMultiplexer.clear()
    }

    override fun render(delta: Float) {
        uiViewport.apply()
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.disposeSafely()
    }
}
