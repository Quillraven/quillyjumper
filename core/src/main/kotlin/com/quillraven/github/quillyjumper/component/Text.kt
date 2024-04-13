package com.quillraven.github.quillyjumper.component

import com.badlogic.gdx.math.Rectangle
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Text(val text: String, val boundary: Rectangle) : Component<Text> {
    override fun type() = Text

    companion object : ComponentType<Text>()
}
