package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.ParallaxBackground
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2

class ParallaxBgdSystem(
    private val gameCamera: OrthographicCamera = inject(),
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject("gameViewport"),
) : IntervalSystem() {
    private val parallaxBgd = ParallaxBackground(gameViewport, vec2(0.2f, 0.2f))

    override fun onTick() {
        parallaxBgd.scrollTo(gameCamera.position.x, -gameCamera.position.y)

        gameViewport.apply()
        batch.use(gameCamera) {
            val paraX = gameCamera.position.x - gameCamera.viewportWidth * 0.5f
            val paraY = gameCamera.position.y - gameCamera.viewportHeight * 0.5f
            parallaxBgd.draw(paraX, paraY, it)
        }
    }

    override fun onDispose() {
        parallaxBgd.disposeSafely()
    }

}
