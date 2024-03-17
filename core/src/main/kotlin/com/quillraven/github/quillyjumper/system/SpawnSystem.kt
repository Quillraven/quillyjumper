package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.TextureAtlasAsset
import com.quillraven.github.quillyjumper.ai.AiEntity
import com.quillraven.github.quillyjumper.ai.GameObjectStateIdle
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import com.quillraven.github.quillyjumper.util.fixtureDefOf
import ktx.app.gdxError
import ktx.box2d.body
import ktx.tiled.height
import ktx.tiled.id
import ktx.tiled.property
import ktx.tiled.width

class SpawnSystem(
    private val physicWorld: PhysicWorld = inject(),
    private val assets: Assets = inject(),
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
        // 1) spawn static ground bodies
        map.forEachCell { cell, cellX, cellY ->
            cell.tile?.objects?.forEach { collObj ->
                spawnGroundBodies(cellX, cellY, collObj)
            }
        }

        // 2) spawn dynamic/kinematic game object bodies
        map.layers.filter { it !is TiledMapTileLayer }
            .forEach { objectLayer ->
                objectLayer.objects.forEach { spawnGameObjectEntity(it) }
            }
    }

    private fun spawnGameObjectEntity(mapObject: MapObject) {
        if (mapObject !is TiledMapTileMapObject) {
            gdxError("Unsupported mapObject $mapObject")
        }

        // spawn physic body
        val gameObjectStr = mapObject.tile.property<String>("GameObject")
        val gameObject = GameObject.valueOf(gameObjectStr)
        val fixtureDefs = OBJECT_FIXTURES[gameObject]
            ?: gdxError("No fixture definitions for $gameObjectStr")
        val x = mapObject.x * UNIT_SCALE
        val y = mapObject.y * UNIT_SCALE
        val body = physicWorld.body(BodyType.DynamicBody) {
            position.set(x, y)
            fixedRotation = true
        }
        fixtureDefs.forEach { fixtureDef ->
            body.createFixture(fixtureDef)
            fixtureDef.shape.dispose()
        }

        // spawn entity
        world.entity {
            body.userData = it
            it += Tiled(gameObject, mapObject.id)
            it += Physic(body)
            // IMPORTANT: to make a sprite flip it must have a region already. Otherwise,
            // the flipX information of the sprite will always be false.
            // Since the AnimationSystem is updating the region of the sprite and is also
            // restoring the flip information, we should set the Sprite region already at this point.
            it += Graphic(sprite(gameObject, AnimationType.IDLE.atlasKey))

            if (gameObject == GameObject.FROG) {
                it += listOf(EntityTag.PLAYER, EntityTag.CAMERA_FOCUS)
                it += Move(max = 8f, timeToMax = 4.5f)
                it += Jump(maxHeight = 3f)
                it += Animation(gdxAnimation(world, gameObject, AnimationType.IDLE))
                it += State(AiEntity(it, world), GameObjectStateIdle)
            }
        }
    }

    private fun sprite(objectID: GameObject, animationType: String): Sprite {
        val atlas = assets[TextureAtlasAsset.GAMEOBJECT]
        val regions = atlas.findRegions("${objectID.atlasKey}/$animationType")
            ?: gdxError("There are no regions for $objectID and $animationType")

        val firstFrame = regions.first()
        val w = firstFrame.regionWidth * UNIT_SCALE
        val h = firstFrame.regionHeight * UNIT_SCALE
        return Sprite(firstFrame).apply { setSize(w, h) }
    }

    private fun spawnGroundBodies(cellX: Int, cellY: Int, collObj: MapObject) {
        when (collObj) {
            is RectangleMapObject -> {
                val body = physicWorld.body(BodyType.StaticBody) {
                    position.set(cellX.toFloat(), cellY.toFloat())
                    fixedRotation = true
                }
                val fixtureDef = fixtureDefOf(collObj)
                body.createFixture(fixtureDef)
                fixtureDef.shape.dispose()
            }
        }
    }

}
