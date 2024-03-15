package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.compareEntityBy
import com.quillraven.github.quillyjumper.GameEvent
import com.quillraven.github.quillyjumper.GameEventListener
import com.quillraven.github.quillyjumper.MapChangeEvent
import com.quillraven.github.quillyjumper.Quillyjumper
import com.quillraven.github.quillyjumper.component.Graphic
import ktx.assets.disposeSafely

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject("gameViewport"),
    private val gameCamera: OrthographicCamera = inject(),
) : IteratingSystem(
    family = family { all(Graphic) },
    comparator = compareEntityBy(Graphic),
), GameEventListener {

    private val mapRenderer = OrthogonalTiledMapRenderer(null, Quillyjumper.UNIT_SCALE, batch)
    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()

    override fun onTick() {
        gameViewport.apply()

        mapRenderer.use(gameCamera) {
            bgdLayers.forEach { mapRenderer.renderTileLayer(it) }

            // render entities
            super.onTick()

            fgdLayers.forEach { mapRenderer.renderTileLayer(it) }
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (sprite) = entity[Graphic]
        sprite.draw(batch)
    }

    private inline fun OrthogonalTiledMapRenderer.use(
        camera: OrthographicCamera,
        block: (OrthogonalTiledMapRenderer) -> Unit
    ) {
        this.setView(camera)
        AnimatedTiledMapTile.updateAnimationBaseTime()
        this.batch.begin()

        block(this)

        this.batch.end()
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                mapRenderer.map = event.tiledMap
                parseMapLayers(event.tiledMap)
            }
        }
    }

    private fun parseMapLayers(tiledMap: TiledMap) {
        bgdLayers.clear()
        fgdLayers.clear()
        var currentLayers = bgdLayers
        tiledMap.layers.forEach { layer ->
            when (layer) {
                is TiledMapTileLayer -> currentLayers += layer

                is MapLayer -> {
                    // we have an ObjectLayer -> switch from background to foreground layer parsing
                    currentLayers = fgdLayers
                }
            }
        }
    }

    override fun onDispose() {
        mapRenderer.disposeSafely()
    }
}
