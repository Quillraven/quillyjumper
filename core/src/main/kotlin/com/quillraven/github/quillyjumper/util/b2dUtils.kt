package com.quillraven.github.quillyjumper.util

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import ktx.app.gdxError
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

fun fixtureDefOf(mapObject: MapObject): FixtureDef {
    return when (mapObject) {
        is RectangleMapObject -> rectangleFixtureDef(mapObject)
        is EllipseMapObject -> ellipseFixtureDef(mapObject)
        is PolygonMapObject -> polygonFixtureDef(mapObject)
        is PolylineMapObject -> polylineFixtureDef(mapObject)
        else -> gdxError("Unsupported MapObject $mapObject")
    }
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

    return if (isEqual(ellipseW, ellipseH, 0.1f)) {
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
        val angleStep = PI2 / numVertices
        val vertices = Array(numVertices) { vertexIdx ->
            val angle = vertexIdx * angleStep
            val offsetX = ellipseW * cos(angle)
            val offsetY = ellipseH * sin(angle)
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
    val boxW = rectW * UNIT_SCALE
    val boxH = rectH * UNIT_SCALE

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
