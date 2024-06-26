package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.tiled.TiledService.Companion.fixtureDefOf
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.app.KtxScreen
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.tiled.propertyOrNull

class LoadingScreen(
    private val game: Quillyjumper,
    private val batch: Batch,
    private val assets: Assets,
    private val gameProperties: GameProperties,
    private val audioService: AudioService,
    private val prefs: GamePreferences,
) : KtxScreen {

    private val uiViewport: Viewport = FitViewport(320f, 180f)
    private val stage = Stage(uiViewport, batch)

    // we need to create a physic world to parse the entity collision objects, which are
    // creating fixture definitions via createLoop/createChain/... methods.
    private val physicWorld = createWorld()

    override fun show() {
        // load all resources and parse collision fixtures for game objects out of TiledMap
        assets.loadAll()
        val tiledMap = assets[MapAsset.OBJECTS]
        parseObjectCollisionShapes(tiledMap)
        assets -= MapAsset.OBJECTS
        Scene2DSkin.defaultSkin = assets[SkinAsset.DEFAULT]

        stage.actors {
            table {
                setFillParent(true)

                label("Touch to continue...") {
                    wrap = true
                    setAlignment(Align.center)

                    this += forever(fadeOut(0.5f) then delay(0.1f) then fadeIn(0.5f))

                    it.pad(10f, 10f, 10f, 10f).expand().fill()
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
        uiViewport.apply()
        stage.act(delta)
        stage.draw()

        if (Gdx.input.justTouched()) {
            log.debug { "Leaving LoadingScreen..." }
            game.removeScreen<LoadingScreen>()
            dispose()

            // create remaining game screens
            game.addScreen(MenuScreen(batch, audioService, game, prefs))
            game.addScreen(GameScreen(batch, assets, gameProperties, audioService, game, prefs))
            game.setScreen<MenuScreen>()
        }
    }

    private fun parseObjectCollisionShapes(tiledMap: TiledMap) {
        val tileSet = tiledMap.tileSets.getTileSet(0)
            ?: gdxError("There is no TileSet in the ${MapAsset.OBJECTS} TiledMap.")

        val tileIterator = tileSet.iterator()
        while (tileIterator.hasNext()) {
            val tile = tileIterator.next()
            val objectFixtureDefs = tile.objects.map { fixtureDefOf(it) }
            if (objectFixtureDefs.isEmpty()) {
                gdxError("No collision shapes defined for tile ${tile.id}")
            }

            val gameObjectStr = tile.propertyOrNull<String>("gameObject")
                ?: gdxError("Missing property 'gameObject' on tile ${tile.id}")
            OBJECT_FIXTURES[GameObject.valueOf(gameObjectStr)] = objectFixtureDefs
        }
    }

    override fun dispose() {
        physicWorld.disposeSafely()
        stage.disposeSafely()
    }

    companion object {
        private val log = logger<LoadingScreen>()
    }

}
