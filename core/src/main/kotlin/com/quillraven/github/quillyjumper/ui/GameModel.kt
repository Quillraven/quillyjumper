package com.quillraven.github.quillyjumper.ui

import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.EntityTag
import com.quillraven.github.quillyjumper.event.EntityDamageEvent
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import ktx.tiled.property

class GameModel(private val world: World) : GameEventListener, PropertyChangeSource() {

    var playerLife by propertyNotify(0)
    var mapName by propertyNotify("")

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                mapName = event.tiledMap.property("name", "MISS_MAP_NAME")
            }

            is EntityDamageEvent -> with(world) {
                if (event.entity has EntityTag.PLAYER) {
                    playerLife = event.life.current.toInt()
                }
            }
        }
    }

}
