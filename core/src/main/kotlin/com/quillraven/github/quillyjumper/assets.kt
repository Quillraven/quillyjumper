package com.quillraven.github.quillyjumper

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ktx.assets.disposeSafely
import ktx.assets.load

enum class MapAsset(val path: String) {
    TEST("maps/test.tmx"),
    OBJECTS("maps/objects.tmx"),
}

class Assets {
    private val assetManager = AssetManager().apply {
        setLoader(TiledMap::class.java, TmxMapLoader())
    }

    operator fun get(asset: MapAsset): TiledMap {
        assetManager.load<TiledMap>(asset.path)
        assetManager.finishLoading()
        return assetManager.get(asset.path)
    }

    fun dispose() {
        assetManager.disposeSafely()
    }

}
