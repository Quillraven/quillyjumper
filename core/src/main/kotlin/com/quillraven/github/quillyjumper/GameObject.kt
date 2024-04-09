package com.quillraven.github.quillyjumper

enum class GameObject {
    FROG,
    SAW,
    ROCK_HEAD,
    CHERRY,
    START_FLAG,
    FINISH_FLAG;

    val atlasKey: String = name.lowercase().replace("_", "-")
}
