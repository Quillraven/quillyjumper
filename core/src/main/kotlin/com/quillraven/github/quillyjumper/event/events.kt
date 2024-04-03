package com.quillraven.github.quillyjumper.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.github.quillraven.fleks.Entity
import com.quillraven.github.quillyjumper.component.Life

sealed interface GameEvent

data class MapChangeEvent(val tiledMap: TiledMap) : GameEvent

data class EntityDamageEvent(val entity: Entity, val life: Life) : GameEvent

data class PlayerMapBottomContactEvent(val player: Entity) : GameEvent

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
