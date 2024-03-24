package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class DamageTaken(var amount: Int) : Component<DamageTaken> {
    override fun type() = DamageTaken

    companion object : ComponentType<DamageTaken>()
}
