@file:JvmName("Lwjgl3Launcher")

package com.quillraven.github.quillyjumper.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.quillraven.github.quillyjumper.Quillyjumper

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(Quillyjumper(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Quilly Jumper")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
