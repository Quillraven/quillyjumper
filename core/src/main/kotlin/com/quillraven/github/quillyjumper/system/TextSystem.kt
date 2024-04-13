package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.quillraven.github.quillyjumper.component.Text
import ktx.graphics.use
import ktx.math.vec2
import ktx.scene2d.Scene2DSkin

class TextSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject("gameViewport"),
    private val uiViewport: Viewport = inject("uiViewport"),
) : IteratingSystem(family { all(Text) }) {

    private val txtLoc = vec2()
    private val txtTopRight = vec2()
    private val bitmapFont = Scene2DSkin.defaultSkin.getFont("Masaaki-Regular-Small")

    override fun onTickEntity(entity: Entity) {
        val (txt, boundary) = entity[Text]

        txtLoc.set(boundary.x, boundary.y)
        txtTopRight.set(boundary.x + boundary.width, boundary.y + boundary.height)

        // convert game coordinates to UI coordinates
        gameViewport.project(txtLoc)
        uiViewport.unproject(txtLoc)
        gameViewport.project(txtTopRight)
        uiViewport.unproject(txtTopRight)
        // y-axis is upside down -> fix it (+10 is just an arbitrary number to move the text a little higher)
        txtLoc.y = uiViewport.worldHeight - txtLoc.y + 10f

        // render text
        uiViewport.apply()
        batch.use(uiViewport.camera) {
            val hAlign = Align.center
            bitmapFont.draw(batch, txt, txtLoc.x, txtLoc.y, txtTopRight.x - txtLoc.x, hAlign, true)
        }
    }

}
