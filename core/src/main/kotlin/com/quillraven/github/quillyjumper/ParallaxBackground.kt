package com.quillraven.github.quillyjumper

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.quillraven.github.quillyjumper.Quillyjumper.Companion.UNIT_SCALE
import ktx.assets.disposeSafely
import ktx.math.vec2

class ParallaxBackground(
    gameViewport: Viewport,
    private val scrollSpeed: Vector2 = vec2(1f, 1f)
) : Disposable {
    private val originUV = vec2(0f, 0f)
    private val originUV2 = vec2(0f, 0f)
    private val grayTexture = wrappedTexture("graphics/background/gray.png")
    private val greenTexture = wrappedTexture("graphics/background/green.png")
    private val bgdSprite = Sprite(greenTexture).apply {
        resize(gameViewport.worldWidth, gameViewport.worldHeight)
    }

    private fun wrappedTexture(internalPath: String) = Texture(internalPath).apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    }

    // background sprite that fills out the entire viewport
    private fun Sprite.resize(worldWidth: Float, worldHeight: Float) {
        setSize(worldWidth, worldHeight)
        // tile the texture over the entire background by keeping its original aspect ratio
        originUV.set(u, v)
        originUV2.set((worldWidth / (texture.width * UNIT_SCALE)), (worldHeight / (texture.height * UNIT_SCALE)))
        u2 = originUV2.x
        v2 = originUV2.y
    }

    fun scrollBy(amountX: Float, amountY: Float) {
        bgdSprite.u += amountX * scrollSpeed.x
        bgdSprite.u2 += amountX * scrollSpeed.x
        bgdSprite.v += amountY * scrollSpeed.y
        bgdSprite.v2 += amountY * scrollSpeed.y
    }

    fun scrollTo(scrollX: Float, scrollY: Float) {
        bgdSprite.u = originUV.x + scrollX * scrollSpeed.x
        bgdSprite.u2 = originUV2.x + scrollX * scrollSpeed.x
        bgdSprite.v = originUV.y + scrollY * scrollSpeed.y
        bgdSprite.v2 = originUV2.y + scrollY * scrollSpeed.y
    }

    fun draw(x: Float, y: Float, batch: Batch) {
        bgdSprite.setPosition(x, y)
        bgdSprite.draw(batch)
    }

    override fun dispose() {
        grayTexture.disposeSafely()
        greenTexture.disposeSafely()
    }
}
