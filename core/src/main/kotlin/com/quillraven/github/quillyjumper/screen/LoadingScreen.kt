package com.quillraven.github.quillyjumper.screen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import ktx.app.KtxScreen
import ktx.app.gdxError
import ktx.tiled.propertyOrNull

class LoadingScreen(private val game: Quillyjumper, private val assets: Assets) : KtxScreen {

    override fun show() {
        val tiledMap = assets[MapAsset.OBJECTS]
        parseObjectCollisionShapes(tiledMap)

        game.removeScreen<LoadingScreen>()
        dispose()
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

            val gameObjectStr = tile.propertyOrNull<String>("GameObject")
                ?: gdxError("Missing property 'GameObject' on tile ${tile.id}")
            OBJECT_FIXTURES[GameObject.valueOf(gameObjectStr)] = objectFixtureDefs
        }
    }

}
