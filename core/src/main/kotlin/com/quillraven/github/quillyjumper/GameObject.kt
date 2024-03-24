package com.quillraven.github.quillyjumper

enum class GameObject {
    FROG,
    SAW;

    val atlasKey: String = name.lowercase()
}
