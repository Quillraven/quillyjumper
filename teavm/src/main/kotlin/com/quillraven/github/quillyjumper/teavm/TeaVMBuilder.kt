package com.quillraven.github.quillyjumper.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(File("../assets"))
            webappPath = File("build/dist").canonicalPath

            htmlTitle = "Quilly Jumper"
            htmlWidth = 640
            htmlHeight = 360
        }

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "com.quillraven.github.quillyjumper.teavm.TeaVMLauncher"
        TeaBuilder.build(tool)
    }
}
