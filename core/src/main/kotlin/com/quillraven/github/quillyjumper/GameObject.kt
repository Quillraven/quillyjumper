package com.quillraven.github.quillyjumper

enum class GameObject {
    FROG,
    SAW,
    ROCK_HEAD;

    val atlasKey: String = name.lowercase().replace("_", "-")
}
