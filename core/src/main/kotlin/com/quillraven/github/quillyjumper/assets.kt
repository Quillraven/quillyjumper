package com.quillraven.github.quillyjumper

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
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

enum class MusicAsset(val path: String) {
    TUTORIAL("audio/tutorial.mp3"),
}

enum class SoundAsset(val path: String) {
    JUMP("audio/jump.mp3"),
}

class Assets : Disposable {

    private val assetManager = AssetManager().apply {
        setLoader(TiledMap::class.java, TmxMapLoader())
    }

    fun loadAll() {
        MapAsset.entries.forEach { assetManager.load<TiledMap>(it.path) }
        TextureAtlasAsset.entries.forEach { assetManager.load<TextureAtlas>(it.path) }
        SoundAsset.entries.forEach { assetManager.load<Sound>(it.path) }
        assetManager.finishLoading()
    }

    operator fun plusAssign(asset: MusicAsset) {
        assetManager.load<Music>(asset.path)
        assetManager.finishLoading()
    }

    operator fun get(asset: MapAsset): TiledMap {
        return assetManager.get(asset.path)
    }

    operator fun get(asset: TextureAtlasAsset): TextureAtlas {
        return assetManager.get(asset.path)
    }

    operator fun get(asset: SoundAsset): Sound {
        return assetManager.get(asset.path)
    }

    operator fun get(asset: MusicAsset): Music {
        return assetManager.get(asset.path)
    }

    operator fun minusAssign(asset: MapAsset) {
        assetManager.unload(asset.path)
    }

    operator fun minusAssign(asset: MusicAsset) {
        assetManager.unload(asset.path)
    }

    override fun dispose() {
        assetManager.disposeSafely()
    }

}
