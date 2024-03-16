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

    operator fun get(asset: MapAsset): TiledMap {
        assetManager.load<TiledMap>(asset.path)
        assetManager.finishLoading()
        return assetManager.get(asset.path)
    }

    operator fun get(asset: TextureAtlasAsset): TextureAtlas {
        assetManager.load<TextureAtlas>(asset.path)
        assetManager.finishLoading()
        return assetManager.get(asset.path)
    }

    operator fun minus(asset: MapAsset) {
        assetManager.unload(asset.path)
    }

    override fun dispose() {
        assetManager.disposeSafely()
    }

}
