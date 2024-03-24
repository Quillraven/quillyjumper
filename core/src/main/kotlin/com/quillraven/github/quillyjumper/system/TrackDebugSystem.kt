package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.Track
import ktx.assets.disposeSafely
import ktx.graphics.use

class TrackDebugSystem(
    private val gameViewport: Viewport = World.inject("gameViewport"),
    private val gameCamera: OrthographicCamera = World.inject(),
) : IteratingSystem(World.family { all(Track) }) {

    private val shapeRenderer = ShapeRenderer()

    override fun onTick() {
        gameViewport.apply()
        shapeRenderer.use(ShapeRenderer.ShapeType.Line, gameCamera) {
            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (trackPoints, closedTrack) = entity[Track]
        for (i in 0 until trackPoints.size - 1) {
            shapeRenderer.line(trackPoints[i], trackPoints[i + 1])
            if (i == trackPoints.size - 2 && closedTrack) {
                shapeRenderer.line(trackPoints[i + 1], trackPoints[0])
            }
        }
    }

    override fun onDispose() {
        shapeRenderer.disposeSafely()
    }

}
