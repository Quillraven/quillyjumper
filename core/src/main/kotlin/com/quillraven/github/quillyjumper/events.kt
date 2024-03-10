package com.quillraven.github.quillyjumper

import com.badlogic.gdx.maps.tiled.TiledMap

sealed interface GameEvent

data class MapChangeEvent(val tiledMap: TiledMap) : GameEvent

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

object GameEventDispatcher {
    private val listeners = mutableListOf<GameEventListener>()

    fun register(listener: GameEventListener) {
        listeners += listener
    }

    fun unregister(listener: GameEventListener) {
        listeners -= listener
    }

    fun fire(event: GameEvent) {
        listeners.forEach { it.onEvent(event) }
    }
}
