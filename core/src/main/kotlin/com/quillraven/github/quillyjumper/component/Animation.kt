package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.componentTypeOf

typealias GdxAnimation = com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>

enum class AnimationType {
    IDLE, RUN, JUMP, FALL, HIT, DOUBLE_JUMP, AGGRO, WAVE;

    val atlasKey: String = name.lowercase()
}

data class Animation(
    var gdxAnimation: GdxAnimation,
    var playMode: PlayMode = PlayMode.LOOP,
    var timer: Float = 0f,
    val type: ComponentType<Animation>,
    var nextAnimation: GdxAnimation? = null,
) : Component<Animation> {
    override fun type() = type

    fun changeAnimation(gdxAnimation: GdxAnimation, playMode: PlayMode = PlayMode.LOOP) {
        this.timer = 0f
        this.gdxAnimation = gdxAnimation
        this.playMode = playMode
    }

    companion object {
        val NORMAL_ANIMATION = componentTypeOf<Animation>()

        // global animation has higher priority than normal animation which means if an entity
        // has a global animation then it is played instead of the normal animation
        val GLOBAL_ANIMATION = componentTypeOf<Animation>()
        const val DEFAULT_FRAME_DURATION = 1 / 15f
    }
}
