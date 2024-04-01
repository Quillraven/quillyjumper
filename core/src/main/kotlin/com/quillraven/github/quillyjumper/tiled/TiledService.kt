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
import com.quillraven.github.quillyjumper.*
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.OBJECT_FIXTURES
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import ktx.app.gdxError
import ktx.box2d.body
import ktx.box2d.box
import ktx.log.logger
import ktx.math.*
import ktx.tiled.*

typealias GdxFloatArray = com.badlogic.gdx.utils.FloatArray

data class FixtureDefUserData(val def: FixtureDef, val userData: String, val size: Vector2)

class TiledService(
    private val world: World,
    private val physicWorld: PhysicWorld,
    private val assets: Assets,
    private val animationService: AnimationService,
) : GameEventListener {

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                spawnEntities(event.tiledMap)
                spawnMapBoundary(event.tiledMap)
            }

            else -> Unit
        }
    }

    private fun spawnMapBoundary(tiledMap: TiledMap) {
        // create three boxes for the map boundary (left, bottom and right edge)
        physicWorld.body(BodyType.StaticBody) {
            val mapW = tiledMap.width * 0.5f
            val mapH = tiledMap.height * 0.5f
            position.set(mapW, mapH)

            var boxW = 3f
            var boxH = mapH * 2f + 20f
            // left edge
            box(boxW, boxH, vec2(-mapW - boxW * 0.5f, mapH)) {
                friction = 0f
                userData = "mapBoundaryLeft"
            }
            // right edge
            box(boxW, boxH, vec2(mapW + boxW * 0.5f, mapH)) {
                friction = 0f
                userData = "mapBoundaryRight"
            }
            // bottom edge
            boxW = mapW * 2f
            boxH = 3f
            box(boxW, boxH, vec2(0f, -mapH - boxH * 0.5f - 5f)) {
                friction = 0f
                userData = "mapBoundaryBottom"
            }
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
        val objectLayers = mapOf(
            -1 to "bgdObjects",
            0 to "objects",
            1 to "fgdObjects",
        )
        objectLayers.forEach { (zIndex, layerName) ->
            map.layer(layerName).objects.forEach { spawnGameObjectEntity(it, zIndex, trackLayer) }
        }
    }

    private fun spawnGameObjectEntity(mapObject: MapObject, zIndex: Int, trackLayer: MapLayer) {
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
            when {
                zIndex < 0 -> it += EntityTag.BACKGROUND
                zIndex > 0 -> it += EntityTag.FOREGROUND
            }
            configureEntityTags(it, tile)
            configureAnimation(it, tile, animationService, gameObject)
            configureState(it, tile, world, animationService)
            configureJump(it, tile)
            configureLife(it, tile)
            configureMove(it, tile)
            configureDamage(it, tile)
            configureTrack(it, mapObject, trackLayer)
            configureAggro(it, tile, gameObject)

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

        fun MapLayer.isObjectsLayer(): Boolean = this.name == "objects"

        private val MapObject.userData: String
            get() = property("userData", "")

        fun fixtureDefOf(mapObject: MapObject): FixtureDefUserData {
            val fixtureDef = when (mapObject) {
                is RectangleMapObject -> rectangleFixtureDef(mapObject, mapObject.userData)
                is EllipseMapObject -> ellipseFixtureDef(mapObject)
                is PolygonMapObject -> polygonFixtureDef(mapObject)
                is PolylineMapObject -> polylineFixtureDef(mapObject)
                else -> gdxError("Unsupported MapObject $mapObject")
            }

            fixtureDef.def.friction = mapObject.property("friction", 0f)
            fixtureDef.def.restitution = mapObject.property("restitution", 0f)
            fixtureDef.def.density = mapObject.property("density", 0f)
            fixtureDef.def.isSensor = mapObject.property("isSensor", false)

            return fixtureDef
        }

        private fun polylineFixtureDef(mapObject: PolylineMapObject): FixtureDefUserData {
            return polygonFixtureDef(mapObject.x, mapObject.y, mapObject.polyline.vertices, false, mapObject.userData)
        }

        private fun polygonFixtureDef(mapObject: PolygonMapObject): FixtureDefUserData {
            return polygonFixtureDef(mapObject.x, mapObject.y, mapObject.polygon.vertices, true, mapObject.userData)
        }

        private fun polygonFixtureDef(
            polyX: Float,
            polyY: Float,
            polyVertices: FloatArray,
            loop: Boolean,
            userData: String,
        ): FixtureDefUserData {
            val x = polyX * UNIT_SCALE
            val y = polyY * UNIT_SCALE
            val vertices = FloatArray(polyVertices.size) { vertexIdx ->
                if (vertexIdx % 2 == 0) {
                    x + polyVertices[vertexIdx] * UNIT_SCALE
                } else {
                    y + polyVertices[vertexIdx] * UNIT_SCALE
                }
            }

            val def = FixtureDef().apply {
                shape = ChainShape().apply {
                    if (loop) {
                        createLoop(vertices)
                    } else {
                        createChain(vertices)
                    }
                }
            }
            return FixtureDefUserData(def, userData, Vector2.Zero)
        }

        private fun ellipseFixtureDef(mapObject: EllipseMapObject): FixtureDefUserData {
            val (x, y, w, h) = mapObject.ellipse
            val ellipseX = x * UNIT_SCALE
            val ellipseY = y * UNIT_SCALE
            val ellipseW = w * UNIT_SCALE / 2f
            val ellipseH = h * UNIT_SCALE / 2f

            return if (MathUtils.isEqual(ellipseW, ellipseH, 0.1f)) {
                // width and height are equal -> return a circle shape
                val def = FixtureDef().apply {
                    shape = CircleShape().apply {
                        position = vec2(ellipseX + ellipseW, ellipseY + ellipseH)
                        radius = ellipseW
                    }
                }
                FixtureDefUserData(def, mapObject.userData, vec2(ellipseW, ellipseH))
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

                val def = FixtureDef().apply {
                    shape = ChainShape().apply {
                        createLoop(vertices)
                    }
                }
                FixtureDefUserData(def, mapObject.userData, vec2(ellipseW, ellipseH))
            }
        }

        // box is centered around body position in Box2D, but we want to have it aligned in a way
        // that the body position is the bottom left corner of the box.
        // That's why we use a 'boxOffset' below.
        private fun rectangleFixtureDef(mapObject: RectangleMapObject, userData: String): FixtureDefUserData {
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

                val def = FixtureDef().apply {
                    shape = ChainShape().apply {
                        createLoop(vertices)
                    }
                }
                return FixtureDefUserData(def, userData, vec2(boxW, boxH))
            }

            // create a box
            val boxW = rectW * UNIT_SCALE * 0.5f
            val boxH = rectH * UNIT_SCALE * 0.5f
            val def = FixtureDef().apply {
                shape = PolygonShape().apply {
                    setAsBox(boxW, boxH, vec2(boxX + boxW, boxY + boxH), 0f)
                }
            }
            return FixtureDefUserData(def, userData, vec2(boxW, boxH))
        }
    }
}
