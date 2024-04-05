package com.quillraven.github.quillyjumper.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.ChainShape
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateContext
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.ai.AiEntity
import com.quillraven.github.quillyjumper.ai.GameObjectState
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.system.USER_DATA_AGGRO_SENSOR
import ktx.app.gdxError
import ktx.math.vec2
import ktx.tiled.*
import kotlin.math.max

fun EntityCreateContext.configureEntityTags(entity: Entity, tile: TiledMapTile) {
    val tagsStr = tile.property<String>("entityTags", "")
    if (tagsStr.isNotBlank()) {
        val tags = tagsStr.split(",").map(EntityTag::valueOf)
        entity += tags
    }
}

fun EntityCreateContext.configureAnimation(
    entity: Entity,
    tile: TiledMapTile,
    animationService: AnimationService,
    gameObject: GameObject
) {
    val hasAnimation = tile.property<Boolean>("hasAnimation", false)
    if (hasAnimation) {
        entity += Animation(
            animationService.gdxAnimation(gameObject, AnimationType.IDLE),
            type = Animation.NORMAL_ANIMATION
        )
    }
}

fun EntityCreateContext.configureState(
    entity: Entity,
    tile: TiledMapTile,
    world: World,
    animationService: AnimationService,
    physicWorld: PhysicWorld,
) {
    val initialState = tile.property<String>("initialState", "")
    if (initialState.isNotBlank()) {
        entity += State(AiEntity(entity, world, animationService, physicWorld), GameObjectState.valueOf(initialState))
    }
}

fun EntityCreateContext.configureJump(entity: Entity, tile: TiledMapTile) {
    val jumpHeight = tile.property<Float>("jumpHeight", 0f)
    if (jumpHeight > 0f) {
        val (body) = entity[Physic]
        val feetFixture = body.fixtureList.firstOrNull { it.userData == "feet" }
        if (feetFixture == null || feetFixture.shape !is ChainShape) {
            gdxError("Jumping entities must have a chain fixture with userdata 'feet'. Tile=${tile.id}")
        }
        val chainShape = feetFixture.shape as ChainShape
        val lowerXY = vec2(100f, 100f)
        val upperXY = vec2(-100f, -100f)
        val vertex = vec2()
        for (i in 0 until chainShape.vertexCount) {
            chainShape.getVertex(i, vertex)
            if (vertex.y <= lowerXY.y && vertex.x <= lowerXY.x) {
                lowerXY.set(vertex)
            } else if (vertex.y >= upperXY.y && vertex.x >= upperXY.x) {
                upperXY.set(vertex)
            }
        }

        if (lowerXY.x == 100f || upperXY.x == -100f) {
            gdxError("Could not calculate feet fixture size of entity $entity and tile ${tile.id}")
        }

        entity += Jump(maxHeight = jumpHeight, lowerFeet = lowerXY, upperFeet = upperXY)
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

fun EntityCreateContext.configureAggro(entity: Entity, tile: TiledMapTile, gameObject: GameObject) {
    val hasAggro = tile.property<Boolean>("hasAggro", false)
    if (hasAggro) {
        val aggroDef = OBJECT_FIXTURES[gameObject]?.first { it.def.isSensor && it.userData == USER_DATA_AGGRO_SENSOR }
            ?: gdxError("There is no aggroSensor for entity $entity")
        val range = max(aggroDef.size.x, aggroDef.size.y)
        entity += Aggro(sourceLocation = entity[Graphic].center.cpy(), range = range)
    }
}
