package com.quillraven.github.quillyjumper.util

import com.badlogic.gdx.math.Ellipse
import com.badlogic.gdx.math.Rectangle

operator fun Rectangle.component1(): Float = this.x
operator fun Rectangle.component2(): Float = this.y
operator fun Rectangle.component3(): Float = this.width
operator fun Rectangle.component4(): Float = this.height

operator fun Ellipse.component1(): Float = this.x
operator fun Ellipse.component2(): Float = this.y
operator fun Ellipse.component3(): Float = this.width
operator fun Ellipse.component4(): Float = this.height
