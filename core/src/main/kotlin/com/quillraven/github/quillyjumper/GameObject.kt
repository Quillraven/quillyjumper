package com.quillraven.github.quillyjumper

enum class GameObject {
    FROG,
    SAW,
    ROCK_HEAD,
    CHERRY;

    val atlasKey: String = name.lowercase().replace("_", "-")
}
