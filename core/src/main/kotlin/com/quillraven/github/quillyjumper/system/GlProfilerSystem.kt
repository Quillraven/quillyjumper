package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.github.quillraven.fleks.IntervalSystem

class GlProfilerSystem : IntervalSystem() {

    private val profiler = GLProfiler(Gdx.graphics).apply { enable() }

    override fun onTick() {
        Gdx.graphics.setTitle("bindings: ${profiler.textureBindings}, drawCalls: ${profiler.drawCalls}, calls: ${profiler.calls}")

        profiler.reset()
    }

}
