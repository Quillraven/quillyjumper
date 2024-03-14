package com.quillraven.github.quillyjumper

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import ktx.app.gdxError
import ktx.math.vec2

fun fixtureDefOf(mapObject: MapObject): FixtureDef {
    return when (mapObject) {
        is RectangleMapObject -> {
            val (rectX, rectY, rectW, rectH) = mapObject.rectangle
            val boxW = rectW * UNIT_SCALE / 2f
            val boxH = rectH * UNIT_SCALE / 2f
            val boxOffset = vec2(
                rectX * UNIT_SCALE + boxW,
                rectY * UNIT_SCALE + boxH
            )

            FixtureDef().apply {
                shape = PolygonShape().apply { setAsBox(boxW, boxH, boxOffset, 0f) }
            }
        }

        is EllipseMapObject -> {
            val (x, y, w, h) = mapObject.ellipse
            val ellipseW = w * UNIT_SCALE / 2f
            val ellipseH = h * UNIT_SCALE / 2f

            if (!MathUtils.isEqual(ellipseW, ellipseH, 0.1f)) {
                gdxError("Ellipse shape is only supported if the width and height are equal")
            }

            FixtureDef().apply {
                shape = CircleShape().apply {
                    position = vec2(x * UNIT_SCALE + ellipseW, y * UNIT_SCALE + ellipseH)
                    radius = ellipseW
                }
            }
        }

        else -> gdxError("Unsupported MapObject $mapObject")
    }
}
