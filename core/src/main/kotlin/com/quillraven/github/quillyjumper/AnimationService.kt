package com.quillraven.github.quillyjumper

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.component.*
import ktx.app.gdxError
import ktx.log.logger

class AnimationService(private val objectAtlas: TextureAtlas) {
    private val animationCache = mutableMapOf<String, GdxAnimation>()

    fun World.entityAnimation(
        entity: Entity,
        type: AnimationType,
        playMode: PlayMode,
        cmpType: ComponentType<Animation>,
    ): Animation {
        val (gameObject) = entity[Tiled]
        val gdxAnimation = gdxAnimation(gameObject, type)

        var aniCmp = entity.getOrNull(cmpType)
        if (aniCmp == null) {
            aniCmp = Animation(gdxAnimation, playMode, type = cmpType)
            entity.configure { it += aniCmp }
        } else {
            aniCmp.changeAnimation(gdxAnimation, playMode)
        }

        val (sprite) = entity[Graphic]
        sprite.updateRegion(gdxAnimation.getKeyFrame(0f))

        return aniCmp
    }

    fun gdxAnimation(gameObject: GameObject, type: AnimationType) = gdxAnimation(gameObject.atlasKey, type)

    fun gdxAnimation(atlasKey: String, type: AnimationType): GdxAnimation {
        val animationAtlasKey = "${atlasKey}/${type.atlasKey}"
        val gdxAnimation = animationCache.getOrPut(animationAtlasKey) {
            val regions = objectAtlas.findRegions(animationAtlasKey)
            if (regions.isEmpty) {
                gdxError("There are no regions for the animation $animationAtlasKey")
            }
            GdxAnimation(Animation.DEFAULT_FRAME_DURATION, regions)
        }

        if (animationCache.size > 100) {
            log.info { "Animation cache is larger than 100" }
        }
        return gdxAnimation
    }

    companion object {
        private val log = logger<AnimationService>()

        fun Sprite.updateRegion(region: TextureRegion) {
            val flipX = isFlipX
            val flipY = isFlipY
            setRegion(region)
            setFlip(flipX, flipY)
        }
    }
}
