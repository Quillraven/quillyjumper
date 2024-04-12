package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.PhysicWorld

class GlProfilerSystem(private val physicWorld: PhysicWorld = inject()) : IntervalSystem() {

    private val profiler = GLProfiler(Gdx.graphics).apply { enable() }

    override fun onTick() {
        Gdx.graphics.setTitle(
            """
            |bindings: ${profiler.textureBindings},
            |drawCalls: ${profiler.drawCalls},
            |calls: ${profiler.calls},
            |fps: ${Gdx.app.graphics.framesPerSecond},
            |entities: ${world.numEntities},
            |bodies: ${physicWorld.bodyCount}
            """.trimMargin().replace(Regex("(\n*)\n"), "$1")
        )

        profiler.reset()
    }

}
