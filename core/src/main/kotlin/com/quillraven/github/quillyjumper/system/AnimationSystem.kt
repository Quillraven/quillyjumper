package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.TextureAtlasAsset
import com.quillraven.github.quillyjumper.component.*
import ktx.app.gdxError
import ktx.log.logger

class AnimationSystem(
    assets: Assets = inject(),
) : IteratingSystem(World.family { all(Animation, Graphic) }) {

    private val objectAtlas = assets[TextureAtlasAsset.GAMEOBJECT]
    private val animationCache = mutableMapOf<String, GdxAnimation>()

    override fun onTickEntity(entity: Entity) {
        val animationCmp = entity[Animation]
        val (gdxAnimation, playMode, timer) = animationCmp
        val (sprite) = entity[Graphic]

        gdxAnimation.playMode = playMode
        sprite.updateRegion(gdxAnimation.getKeyFrame(timer))
        animationCmp.timer += deltaTime
    }

    fun entityAnimation(entity: Entity, type: AnimationType, playMode: PlayMode) {
        val (gameObject) = entity[Tiled]
        val gdxAnimation = gdxAnimation(gameObject, type)

        val aniCmp = entity[Animation]
        aniCmp.gdxAnimation = gdxAnimation
        aniCmp.playMode = playMode
        val (sprite) = entity[Graphic]
        sprite.updateRegion(gdxAnimation.getKeyFrame(0f))
    }

    private fun Sprite.updateRegion(region: TextureRegion) {
        val flipX = isFlipX
        val flipY = isFlipY
        setRegion(region)
        setFlip(flipX, flipY)
    }

    fun gdxAnimation(
        gameObject: GameObject,
        type: AnimationType
    ): GdxAnimation {
        val animationAtlasKey = "${gameObject.atlasKey}/${type.atlasKey}"
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
        private val log = logger<AnimationSystem>()
    }

}
