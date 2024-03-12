package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld
import ktx.assets.disposeSafely

class PhysicRenderDebugSystem(
    private val physicWorld: PhysicWorld = inject(),
    private val gameViewport: Viewport = inject("gameViewport"),
    private val gameCamera: OrthographicCamera = inject(),
) : IntervalSystem() {

    private val b2dRenderer = Box2DDebugRenderer().apply {
        SHAPE_STATIC.set(1f, 0f, 0f, 1f)
    }

    override fun onTick() {
        gameViewport.apply()
        b2dRenderer.render(physicWorld, gameCamera.combined)
    }

    override fun onDispose() {
        b2dRenderer.disposeSafely()
    }

}
