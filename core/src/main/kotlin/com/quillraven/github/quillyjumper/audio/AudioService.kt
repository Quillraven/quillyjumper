package com.quillraven.github.quillyjumper.audio

import com.badlogic.gdx.audio.Music
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.MusicAsset
import com.quillraven.github.quillyjumper.SoundAsset
import com.quillraven.github.quillyjumper.event.*
import ktx.log.logger
import ktx.tiled.propertyOrNull

private data class MusicResource(val music: Music, val asset: MusicAsset)

class AudioService(
    private val assets: Assets,
    private var soundVolume: Float = 1f,
    private var musicVolume: Float = 1f,
) : GameEventListener {

    private val soundQueue = mutableSetOf<SoundAsset>()
    private var currentMusicResource: MusicResource? = null

    init {
        GameEventDispatcher.register(this)
    }

    fun play(asset: SoundAsset) {
        log.debug { "Play sound $asset" }

        soundQueue += asset
    }

    fun play(asset: MusicAsset, loop: Boolean = true) {
        log.debug { "Play music $asset" }

        if (currentMusicResource?.asset == asset) {
            // given music is already playing -> do nothing
            return
        }

        // stop current music if there is any
        currentMusicResource?.let { musicResource ->
            musicResource.music.stop()
            assets -= musicResource.asset
        }

        // play new music
        assets += asset
        val music = assets[asset]
        currentMusicResource = MusicResource(music, asset)
        music.volume = musicVolume
        music.isLooping = loop
        music.play()
    }

    fun update() {
        soundQueue.forEach { soundAsset ->
            assets[soundAsset].play(soundVolume)
        }
        soundQueue.clear()
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                event.tiledMap.propertyOrNull<String>("musicAsset")?.let { musicAssetStr ->
                    play(MusicAsset.valueOf(musicAssetStr))
                }
            }

            is PlayerItemCollectEvent -> {
                when (event.collectableType) {
                    GameObject.FINISH_FLAG -> play(MusicAsset.STAGE_CLEAR, loop = false)
                    GameObject.CHERRY -> play(SoundAsset.CHERRY)
                    else -> Unit
                }
            }

            else -> Unit
        }
    }

    companion object {
        private val log = logger<AudioService>()
    }
}
