package com.quillraven.github.quillyjumper

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import ktx.assets.load

enum class MapAsset(val path: String) {
    TEST("maps/test.tmx"),
    OBJECTS("maps/objects.tmx"),
}

enum class TextureAtlasAsset(val path: String) {
    GAMEOBJECT("graphics/gameobject.atlas"),
}

class Assets : Disposable {

    private val assetManager = AssetManager().apply {
        setLoader(TiledMap::class.java, TmxMapLoader())
    }

    fun loadAll() {
        MapAsset.entries.forEach { assetManager.load<TiledMap>(it.path) }
        TextureAtlasAsset.entries.forEach { assetManager.load<TextureAtlas>(it.path) }
        assetManager.finishLoading()
    }

    operator fun get(asset: MapAsset): TiledMap {
        return assetManager.get(asset.path)
    }

    operator fun get(asset: TextureAtlasAsset): TextureAtlas {
        return assetManager.get(asset.path)
    }

    operator fun minus(asset: MapAsset) {
        assetManager.unload(asset.path)
    }

    override fun dispose() {
        assetManager.disposeSafely()
    }

}
