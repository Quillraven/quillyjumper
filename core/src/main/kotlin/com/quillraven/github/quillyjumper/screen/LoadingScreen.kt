package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.tiled.TiledService.Companion.fixtureDefOf
import ktx.app.KtxScreen
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.scene2d.Scene2DSkin
import ktx.tiled.propertyOrNull

class LoadingScreen(
    private val game: Quillyjumper,
    private val batch: Batch,
    private val assets: Assets,
    private val gameProperties: GameProperties,
    private val audioService: AudioService,
) : KtxScreen {

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

        game.removeScreen<LoadingScreen>()
        dispose()

        // create remaining game screens
        game.addScreen(GameScreen(batch, assets, gameProperties, audioService))
        game.setScreen<GameScreen>()
    }

    private fun parseObjectCollisionShapes(tiledMap: TiledMap) {
        val tileSet = tiledMap.tileSets.getTileSet(0)
            ?: gdxError("There is no TileSet in the ${MapAsset.OBJECTS} TiledMap.")
        val firstGid = tileSet.propertyOrNull<Int>("firstgid")
            ?: gdxError("Tileset $tileSet does not have a 'firstgid' property")

        for (i in 0 until tileSet.size()) {
            val tileID = firstGid + i
            val tile = tileSet.getTile(tileID)
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
    }

}
