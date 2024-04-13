package com.quillraven.github.quillyjumper

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader.ShaderProgramParameter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.quillraven.github.quillyjumper.tiled.TiledLoader
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.assets.load

enum class MapAsset(val path: String, val unlockMap: MapAsset? = null) {
    TEST("maps/test.tmx"),
    MAP_2("maps/map2.tmx"),
    MAP_1("maps/map1.tmx", MAP_2),
    TUTORIAL("maps/tutorial.tmx", MAP_1),
    OBJECTS("maps/objects.tmx");

    val displayName: String = this.name.replace("_", " ")
}

enum class TextureAtlasAsset(val path: String) {
    GAMEOBJECT("graphics/gameobject.atlas"),
}

enum class MusicAsset(val path: String) {
    TUTORIAL("audio/tutorial.mp3"),
    STAGE_CLEAR("audio/stage-clear.mp3"),
    MAIN_MENU("audio/main-menu.wav"),
    MAP_1("audio/map1.wav"),
}

enum class SoundAsset(val path: String) {
    JUMP("audio/jump.wav"),
    HURT("audio/hurt.wav"),
    CHERRY("audio/cherry.wav"),
    DEATH("audio/death.wav"),
}

enum class SkinAsset(val path: String) {
    DEFAULT("ui/skin.json"),
}

enum class ShaderAsset(val vertexShader: String, val fragmentShader: String) {
    FLASH("shader/default.vert", "shader/flash.frag")
}

class Assets : Disposable {

    private val assetManager = AssetManager().apply {
        setLoader(TiledMap::class.java, TiledLoader(this.fileHandleResolver))
    }

    fun loadAll() {
        MapAsset.entries.forEach { assetManager.load<TiledMap>(it.path) }
        TextureAtlasAsset.entries.forEach { assetManager.load<TextureAtlas>(it.path) }
        SoundAsset.entries.forEach { assetManager.load<Sound>(it.path) }
        SkinAsset.entries.forEach { assetManager.load<Skin>(it.path) }
        ShaderAsset.entries.forEach { shaderAsset ->
            assetManager.load<ShaderProgram>(shaderAsset.name, ShaderProgramParameter().apply {
                vertexFile = shaderAsset.vertexShader
                fragmentFile = shaderAsset.fragmentShader
            })
        }
        assetManager.finishLoading()

        // verify that all shaders compiled correctly
        val shaderErrors = ShaderAsset.entries
            .map { it to this[it] }
            .filterNot { (_, shader) -> shader.isCompiled }
            .map { (shaderAsset, failedShader) ->
                "Shader $shaderAsset failed to compile: ${failedShader.log}"
            }
        if (shaderErrors.isNotEmpty()) {
            gdxError("Shader compilation errors:\n ${shaderErrors.joinToString("\n\n\n")}")
        }
    }

    operator fun plusAssign(asset: MusicAsset) {
        assetManager.load<Music>(asset.path)
        assetManager.finishLoading()
    }

    operator fun get(asset: MapAsset): TiledMap = assetManager.get(asset.path)

    operator fun get(asset: TextureAtlasAsset): TextureAtlas = assetManager.get(asset.path)

    operator fun get(asset: SoundAsset): Sound = assetManager.get(asset.path)

    operator fun get(asset: MusicAsset): Music = assetManager.get(asset.path)

    operator fun get(asset: SkinAsset): Skin = assetManager.get(asset.path)

    operator fun get(asset: ShaderAsset): ShaderProgram = assetManager.get(asset.name)

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
