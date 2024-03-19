package com.quillraven.github.quillyjumper

import com.badlogic.gdx.Application
import com.badlogic.gdx.utils.ObjectMap

data class GameProperties(
    val soundVolume: Float,
    val musicVolume: Float,
    val debugPhysic: Boolean,
    val enableProfiling: Boolean,
    val logLevel: Int,
)

fun ObjectMap<String, String>.toGameProperties() = GameProperties(
    soundVolume = getOrDefault("soundVolume", 1f),
    musicVolume = getOrDefault("musicVolume", 1f),
    debugPhysic = getOrDefault("debugPhysic", false),
    enableProfiling = getOrDefault("enableProfiling", false),
    logLevel = getOrDefault("logLevel", Application.LOG_INFO),
)

private inline fun <reified T> ObjectMap<String, String>.getOrDefault(key: String, defaultValue: T): T {
    val strValue = this.get(key) ?: return defaultValue

    return when (T::class) {
        Int::class -> (strValue.toInt() as T)
        Float::class -> (strValue.toFloat() as T)
        Boolean::class -> (strValue.toBoolean() as T)
        else -> (strValue as T)
    }
}
