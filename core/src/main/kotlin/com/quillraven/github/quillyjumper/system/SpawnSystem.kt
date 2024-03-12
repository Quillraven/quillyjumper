package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.component.Physic
import ktx.box2d.body
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width

class SpawnSystem(
    private val physicWorld: PhysicWorld = inject()
) : IntervalSystem(enabled = false), GameEventListener {

    override fun onTick() = Unit

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> spawnEntities(event.tiledMap)
        }
    }

    private inline fun TiledMap.forEachCell(action: (Cell, Int, Int) -> Unit) {
        val mapWidth = this.width
        val mapHeight = this.height
        this.layers.filterIsInstance<TiledMapTileLayer>()
            .forEach { layer ->
                for (x in 0 until mapWidth) {
                    for (y in 0 until mapHeight) {
                        val cell = layer.getCell(x, y)
                        cell?.let { action(it, x, y) }
                    }
                }
            }
    }

    private fun spawnEntities(map: TiledMap) {
        map.forEachCell { cell, cellX, cellY ->
            cell.tile?.objects?.forEach { collObj ->
                spawnEntity(cellX, cellY, collObj)
            }
        }
    }

    private fun spawnEntity(cellX: Int, cellY: Int, collObj: MapObject) {
        when (collObj) {
            is RectangleMapObject -> {
                val (rectX, rectY, rectW, rectH) = collObj.rectangle

                // spawn physic body
                val body = physicWorld.body(BodyType.StaticBody) {
                    position.set(cellX.toFloat(), cellY.toFloat())
                    box(
                        rectW * UNIT_SCALE, rectH * UNIT_SCALE,
                        vec2(
                            rectX * UNIT_SCALE + rectW * UNIT_SCALE * 0.5f,
                            rectY * UNIT_SCALE + rectH * UNIT_SCALE * 0.5f
                        )
                    )
                    fixedRotation = true
                }

                // create entity
                world.entity {
                    body.userData = it
                    it += Physic(body)
                }
            }
        }
    }

}
