package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.component.EntityTag.CAMERA_FOCUS
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width
import kotlin.math.max

class CameraSystem(
    private val camera: OrthographicCamera = inject()
) : IteratingSystem(World.family { all(Graphic, CAMERA_FOCUS) }), GameEventListener {

    private val mapBoundaries = vec2(0f, 0f)

    override fun onTickEntity(entity: Entity) {
        val (sprite) = entity[Graphic]
        val spriteW = sprite.width * 0.5f
        val spriteH = sprite.height * 0.5f

        var newCamX = sprite.x + spriteW
        var newCamY = sprite.y + spriteH
        if (!mapBoundaries.isZero) {
            val viewportW = camera.viewportWidth * 0.5f
            val viewportH = camera.viewportHeight * 0.5f

            newCamX = newCamX.coerceIn(viewportW, max(viewportW, mapBoundaries.x - viewportW))
            newCamY = newCamY.coerceIn(viewportH, max(viewportH, mapBoundaries.y - viewportH))
        }

        camera.position.set(newCamX, newCamY, 0f)
        camera.update()
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                mapBoundaries.set(event.tiledMap.width.toFloat(), event.tiledMap.height.toFloat())
            }

            else -> Unit
        }
    }

}
