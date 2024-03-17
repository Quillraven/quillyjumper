package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.World
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.system.AnimationSystem

typealias GdxAnimation = com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>

fun gdxAnimation(world: World, gameObject: GameObject, type: AnimationType): GdxAnimation {
    // TODO can we optimize it to retrieve the system directly without looping through all systems?
    return world.system<AnimationSystem>().gdxAnimation(gameObject, type)
}

enum class AnimationType {
    IDLE, RUN, JUMP, FALL, HIT, DOUBLE_JUMP;

    val atlasKey: String = name.lowercase()
}

data class Animation(
    var gdxAnimation: GdxAnimation,
    var playMode: PlayMode = PlayMode.LOOP,
    var timer: Float = 0f,
) : Component<Animation> {
    override fun type() = Animation

    companion object : ComponentType<Animation>() {
        const val DEFAULT_FRAME_DURATION = 1 / 15f
    }
}
