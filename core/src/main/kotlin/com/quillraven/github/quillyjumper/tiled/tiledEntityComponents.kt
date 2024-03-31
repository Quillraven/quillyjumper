package com.quillraven.github.quillyjumper.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateContext
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.ai.AiEntity
import com.quillraven.github.quillyjumper.ai.GameObjectState
import com.quillraven.github.quillyjumper.component.*
import ktx.app.gdxError
import ktx.math.vec2
import ktx.tiled.*

fun EntityCreateContext.configureEntityTags(entity: Entity, tile: TiledMapTile) {
    val tagsStr = tile.property<String>("entityTags", "")
    if (tagsStr.isNotBlank()) {
        val tags = tagsStr.split(",").map(EntityTag::valueOf)
        entity += tags
    }
}

fun EntityCreateContext.configureAnimation(entity: Entity, tile: TiledMapTile, world: World, gameObject: GameObject) {
    val hasAnimation = tile.property<Boolean>("hasAnimation", false)
    if (hasAnimation) {
        entity += Animation(gdxAnimation(world, gameObject, AnimationType.IDLE))
    }
}

fun EntityCreateContext.configureState(entity: Entity, tile: TiledMapTile, world: World) {
    val initialState = tile.property<String>("initialState", "")
    if (initialState.isNotBlank()) {
        entity += State(AiEntity(entity, world), GameObjectState.valueOf(initialState))
    }
}

fun EntityCreateContext.configureJump(entity: Entity, tile: TiledMapTile) {
    val jumpHeight = tile.property<Float>("jumpHeight", 0f)
    if (jumpHeight > 0f) {
        entity += Jump(maxHeight = jumpHeight)
    }
}

fun EntityCreateContext.configureLife(entity: Entity, tile: TiledMapTile) {
    val life = tile.property<Int>("life", 0)
    if (life > 0) {
        entity += Life(max = life)
    }
}

fun EntityCreateContext.configureMove(entity: Entity, tile: TiledMapTile) {
    val speed = tile.property<Float>("speed", 0f)
    if (speed > 0f) {
        val timeToMaxSpeed = tile.property<Float>("timeToMaxSpeed", 0f)
        entity += Move(max = speed, timeToMax = timeToMaxSpeed.coerceAtLeast(0.1f))
    }
}

fun EntityCreateContext.configureDamage(entity: Entity, tile: TiledMapTile) {
    val damage = tile.property<Int>("damage", 0)
    if (damage > 0) {
        entity += Damage(damage)
    }
}

fun EntityCreateContext.configureTrack(entity: Entity, mapObject: TiledMapTileMapObject, trackLayer: MapLayer) {
    val hasTrack = mapObject.propertyOrNull<Boolean>("hasTrack") ?: mapObject.tile.property<Boolean>("hasTrack", false)
    if (hasTrack) {
        entity += trackLayer.trackCmpOf(mapObject)
    }
}

private fun MapLayer.trackCmpOf(mapObject: MapObject): Track {
    objects.forEach { layerObject ->
        val lineVertices = when (layerObject) {
            is PolylineMapObject -> GdxFloatArray(layerObject.polyline.transformedVertices)
            is PolygonMapObject -> GdxFloatArray(layerObject.polygon.transformedVertices)
            else -> gdxError("Only Polyline and Polygon map objects are supported for tracks: $layerObject")
        }
        val rectVertices = GdxFloatArray(
            floatArrayOf(
                mapObject.x, mapObject.y,
                mapObject.x + mapObject.width, mapObject.y,
                mapObject.x + mapObject.width, mapObject.y + mapObject.height,
                mapObject.x, mapObject.y + mapObject.height
            )
        )

        if (Intersector.intersectPolygons(lineVertices, rectVertices)) {
            // found related track -> convert track vertices to world unit vertices
            val trackPoints = mutableListOf<Vector2>()
            for (i in 0 until lineVertices.size step 2) {
                val vertexX = lineVertices[i] * UNIT_SCALE
                val vertexY = lineVertices[i + 1] * UNIT_SCALE
                trackPoints += vec2(vertexX, vertexY)
            }
            return Track(trackPoints, closedTrack = layerObject is PolygonMapObject)
        }
    }

    gdxError("There is no related track for MapObject ${mapObject.id}")
}

fun EntityCreateContext.configureAggro(entity: Entity, tile: TiledMapTile) {
    val hasAggro = tile.property<Boolean>("hasAggro", false)
    if (hasAggro) {
        entity += Aggro(sourceLocation = entity[Graphic].center.cpy())
    }
}
