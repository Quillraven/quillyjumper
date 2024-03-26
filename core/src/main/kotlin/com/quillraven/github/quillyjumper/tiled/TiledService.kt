package com.quillraven.github.quillyjumper.tiled

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.PhysicWorld
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.TextureAtlasAsset
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.component.Tiled
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import ktx.app.gdxError
import ktx.box2d.body
import ktx.log.logger
import ktx.math.*
import ktx.tiled.*

typealias GdxFloatArray = com.badlogic.gdx.utils.FloatArray

data class FixtureDefUserData(val def: FixtureDef, val userData: String)

class TiledService(
    private val world: World,
    private val physicWorld: PhysicWorld,
    private val assets: Assets,
) : GameEventListener {

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> spawnEntities(event.tiledMap)
            else -> Unit
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
        val trackLayer = map.layer("tracks")
        map.layer("objects").objects.forEach { spawnGameObjectEntity(it, trackLayer) }
    }

    private fun spawnGameObjectEntity(mapObject: MapObject, trackLayer: MapLayer) {
        if (mapObject !is TiledMapTileMapObject) {
            gdxError("Unsupported mapObject $mapObject")
        }

        // spawn physic body
        val tile = mapObject.tile
        val bodyType = BodyType.valueOf(tile.property<String>("bodyType", BodyType.DynamicBody.name))
        val gameObjectStr = tile.property<String>("gameObject")
        val gameObject = GameObject.valueOf(gameObjectStr)
        val fixtureDefs = OBJECT_FIXTURES[gameObject]
            ?: gdxError("No fixture definitions for $gameObjectStr")
        val x = mapObject.x * UNIT_SCALE
        val y = mapObject.y * UNIT_SCALE
        val body = physicWorld.body(bodyType) {
            position.set(x, y)
            fixedRotation = true
        }
        fixtureDefs.forEach { fixtureDef ->
            body.createFixture(fixtureDef.def).userData = fixtureDef.userData
            // don't dispose the shape because we still need it for future entities that are created
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
            it += Graphic(sprite(gameObject, AnimationType.IDLE.atlasKey, body.position))
            configureEntityTags(it, tile)
            configureAnimation(it, tile, world, gameObject)
            configureState(it, tile, world)
            configureJump(it, tile)
            configureLife(it, tile)
            configureMove(it, tile)
            configureDamage(it, tile)
            configureTrack(it, mapObject, trackLayer)

            log.debug {
                """Spawning entity with:
                | MapObjectId: ${mapObject.id}
                | TileId: ${tile.id}
                | GameObject: $gameObject
            """.trimMargin().replace(Regex("(\n*)\n"), "$1")
            }
        }
    }

    private fun sprite(objectID: GameObject, animationType: String, position: Vector2): Sprite {
        val atlas = assets[TextureAtlasAsset.GAMEOBJECT]
        val regions = atlas.findRegions("${objectID.atlasKey}/$animationType")
            ?: gdxError("There are no regions for $objectID and $animationType")

        val firstFrame = regions.first()
        val w = firstFrame.regionWidth * UNIT_SCALE
        val h = firstFrame.regionHeight * UNIT_SCALE
        return Sprite(firstFrame).apply {
            setPosition(position.x, position.y)
            setSize(w, h)
        }
    }

    private fun spawnGroundBodies(cellX: Int, cellY: Int, collObj: MapObject) {
        when (collObj) {
            is RectangleMapObject -> {
                val body = physicWorld.body(BodyType.StaticBody) {
                    position.set(cellX.toFloat(), cellY.toFloat())
                    fixedRotation = true
                }
                val fixtureDef = fixtureDefOf(collObj)
                body.createFixture(fixtureDef.def).userData = fixtureDef.userData
                fixtureDef.def.shape.dispose()
            }
        }
    }

    companion object {
        private val log = logger<TiledService>()

        fun fixtureDefOf(mapObject: MapObject): FixtureDefUserData {
            val fixtureDef = when (mapObject) {
                is RectangleMapObject -> rectangleFixtureDef(mapObject)
                is EllipseMapObject -> ellipseFixtureDef(mapObject)
                is PolygonMapObject -> polygonFixtureDef(mapObject)
                is PolylineMapObject -> polylineFixtureDef(mapObject)
                else -> gdxError("Unsupported MapObject $mapObject")
            }

            fixtureDef.friction = mapObject.property("friction", 0f)
            fixtureDef.restitution = mapObject.property("restitution", 0f)
            fixtureDef.density = mapObject.property("density", 0f)
            fixtureDef.isSensor = mapObject.property("isSensor", false)

            return FixtureDefUserData(fixtureDef, mapObject.property("userData", ""))
        }

        private fun polylineFixtureDef(mapObject: PolylineMapObject): FixtureDef {
            return polygonFixtureDef(mapObject.x, mapObject.y, mapObject.polyline.vertices, false)
        }

        private fun polygonFixtureDef(mapObject: PolygonMapObject): FixtureDef {
            return polygonFixtureDef(mapObject.x, mapObject.y, mapObject.polygon.vertices, true)
        }

        private fun polygonFixtureDef(
            polyX: Float,
            polyY: Float,
            polyVertices: FloatArray,
            loop: Boolean,
        ): FixtureDef {
            val x = polyX * UNIT_SCALE
            val y = polyY * UNIT_SCALE
            val vertices = FloatArray(polyVertices.size) { vertexIdx ->
                if (vertexIdx % 2 == 0) {
                    x + polyVertices[vertexIdx] * UNIT_SCALE
                } else {
                    y + polyVertices[vertexIdx] * UNIT_SCALE
                }
            }

            return FixtureDef().apply {
                shape = ChainShape().apply {
                    if (loop) {
                        createLoop(vertices)
                    } else {
                        createChain(vertices)
                    }
                }
            }
        }

        private fun ellipseFixtureDef(mapObject: EllipseMapObject): FixtureDef {
            val (x, y, w, h) = mapObject.ellipse
            val ellipseX = x * UNIT_SCALE
            val ellipseY = y * UNIT_SCALE
            val ellipseW = w * UNIT_SCALE / 2f
            val ellipseH = h * UNIT_SCALE / 2f

            return if (MathUtils.isEqual(ellipseW, ellipseH, 0.1f)) {
                // width and height are equal -> return a circle shape
                FixtureDef().apply {
                    shape = CircleShape().apply {
                        position = vec2(ellipseX + ellipseW, ellipseY + ellipseH)
                        radius = ellipseW
                    }
                }
            } else {
                // width and height are not equal -> return an ellipse shape (=polygon with 'numVertices' vertices)
                val numVertices = 20
                val angleStep = MathUtils.PI2 / numVertices
                val vertices = Array(numVertices) { vertexIdx ->
                    val angle = vertexIdx * angleStep
                    val offsetX = ellipseW * MathUtils.cos(angle)
                    val offsetY = ellipseH * MathUtils.sin(angle)
                    vec2(ellipseX + ellipseW + offsetX, ellipseY + ellipseH + offsetY)
                }

                FixtureDef().apply {
                    shape = ChainShape().apply {
                        createLoop(vertices)
                    }
                }
            }
        }

        // box is centered around body position in Box2D, but we want to have it aligned in a way
        // that the body position is the bottom left corner of the box.
        // That's why we use a 'boxOffset' below.
        private fun rectangleFixtureDef(mapObject: RectangleMapObject): FixtureDef {
            val (rectX, rectY, rectW, rectH) = mapObject.rectangle
            val boxX = rectX * UNIT_SCALE
            val boxY = rectY * UNIT_SCALE

            if (mapObject.property("isChain", false)) {
                val boxW = rectW * UNIT_SCALE
                val boxH = rectH * UNIT_SCALE

                // create a chain shaped box
                val vertices = arrayOf(
                    vec2(boxX, boxY),
                    vec2(boxX + boxW, boxY),
                    vec2(boxX + boxW, boxY + boxH),
                    vec2(boxX, boxY + boxH),
                )

                return FixtureDef().apply {
                    shape = ChainShape().apply {
                        createLoop(vertices)
                    }
                }
            }

            // create a box
            val boxW = rectW * UNIT_SCALE * 0.5f
            val boxH = rectH * UNIT_SCALE * 0.5f
            return FixtureDef().apply {
                shape = PolygonShape().apply {
                    setAsBox(boxW, boxH, vec2(boxX + boxW, boxY + boxH), 0f)
                }
            }
        }
    }
}
