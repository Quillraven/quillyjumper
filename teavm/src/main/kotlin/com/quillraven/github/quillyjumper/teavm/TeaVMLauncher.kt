@file:JvmName("TeaVMLauncher")

package com.quillraven.github.quillyjumper.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import com.quillraven.github.quillyjumper.Quillyjumper

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 0
        height = 0
    }
    TeaApplication(Quillyjumper(), config)
}
