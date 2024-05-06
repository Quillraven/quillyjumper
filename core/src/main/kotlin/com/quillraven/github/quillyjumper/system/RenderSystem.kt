package com.quillraven.github.quillyjumper.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.MutableEntityBag
import com.github.quillraven.fleks.collection.compareEntityBy
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.Quillyjumper
import com.quillraven.github.quillyjumper.ShaderAsset
import com.quillraven.github.quillyjumper.component.Flash
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.event.GameEvent
import com.quillraven.github.quillyjumper.event.GameEventListener
import com.quillraven.github.quillyjumper.event.MapChangeEvent
import com.quillraven.github.quillyjumper.tiled.TiledService.Companion.isObjectsLayer
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.tiled.use

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject("gameViewport"),
    private val uiViewport: Viewport = inject("uiViewport"),
    private val stage: Stage = inject(),
    private val gameCamera: OrthographicCamera = inject(),
    assets: Assets = inject()
) : IteratingSystem(family = family { all(Graphic) }, comparator = compareEntityBy(Graphic)), GameEventListener {

    private val mapRenderer = OrthogonalTiledMapRenderer(null, Quillyjumper.UNIT_SCALE, batch)
    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()

    private val bgdEntities = MutableEntityBag()
    private val entities = MutableEntityBag()
    private val fgdEntities = MutableEntityBag()
    private val tmpEntities = MutableEntityBag()

    private val flashShader = assets[ShaderAsset.FLASH]
    private val uLocFlashColor = flashShader.getUniformLocation("u_FlashColor")
    private val uLocFlashWeight = flashShader.getUniformLocation("u_FlashWeight")

    override fun onTick() {
        // split render entities into three parts (background, normal and foreground entities)
        onSort()
        family.entities.partitionTo(bgdEntities, tmpEntities) { it[Graphic].z < 0 }
        tmpEntities.partitionTo(fgdEntities, entities) { it[Graphic].z > 0 }

        // game rendering
        gameViewport.apply()
        mapRenderer.use(gameCamera) {
            // background rendering
            bgdEntities.forEach { onTickEntity(it) }
            batch.resetShader()
            bgdLayers.forEach { mapRenderer.renderTileLayer(it) }

            // middle layer rendering
            entities.forEach { onTickEntity(it) }
            batch.resetShader()

            // foreground rendering
            fgdLayers.forEach { mapRenderer.renderTileLayer(it) }
            fgdEntities.forEach { onTickEntity(it) }
            batch.resetShader()
        }

        // ui rendering
        uiViewport.apply()
        stage.act(deltaTime)
        stage.draw()
    }

    override fun onTickEntity(entity: Entity) {
        val flashCmp = entity.getOrNull(Flash)
        if (flashCmp != null && flashCmp.doFlash) {
            if (batch.shader != flashShader) {
                batch.shader = flashShader
            }
            flashShader.use {
                flashShader.setUniformf(uLocFlashColor, flashCmp.color)
                flashShader.setUniformf(uLocFlashWeight, flashCmp.weight)
            }
        } else {
            batch.resetShader()
        }

        entity[Graphic].sprite.draw(batch)
    }

    private fun Batch.resetShader() {
        if (shader != null) {
            shader = null
        }
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is MapChangeEvent -> {
                mapRenderer.map = event.tiledMap
                parseMapLayers(event.tiledMap)
            }

            else -> Unit
        }
    }

    private fun parseMapLayers(tiledMap: TiledMap) {
        bgdLayers.clear()
        fgdLayers.clear()
        var currentLayers = bgdLayers
        tiledMap.layers.forEach { layer ->
            when {
                layer is TiledMapTileLayer -> currentLayers += layer

                layer.isObjectsLayer() -> {
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
