import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.AnimationService
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.TextureAtlasAsset
import com.quillraven.github.quillyjumper.component.Animation
import com.quillraven.github.quillyjumper.component.Animation.Companion.NORMAL_ANIMATION
import com.quillraven.github.quillyjumper.component.AnimationType
import com.quillraven.github.quillyjumper.component.Graphic
import com.quillraven.github.quillyjumper.component.Tiled
import com.quillraven.github.quillyjumper.system.AnimationSystem
import com.quillraven.github.quillyjumper.system.RenderSystem
import ktx.app.KtxScreen
import ktx.graphics.use

class RenderTestScreen : KtxScreen {

    private val batch: Batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(16f, 9f, camera)
    private val uiViewport: Viewport = FitViewport(320f, 180f)
    private val stage = Stage(uiViewport, batch)
    private val assets = Assets().apply { loadAll() }
    private val animationService = AnimationService(assets[TextureAtlasAsset.GAMEOBJECT])
    private val frogSprite = Sprite(textureRegion("frog/idle")).apply {
        setPosition(4f, 1f)
        setSize(1f, 1f)
    }
    private val frogSpriteFlipped = Sprite(textureRegion("frog/idle")).apply {
        setPosition(4f, 2f)
        setSize(1f, 1f)
        setFlip(true, false)
    }
    private val world = configureWorld {
        injectables {
            add(assets)
            add(batch)
            add("gameViewport", viewport)
            add("uiViewport", viewport)
            add(stage)
            add(camera)
        }

        systems {
            add(AnimationSystem())
            add(RenderSystem())
        }
    }

    init {
        // simple sprite entities
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(3f, 1f)
                setSize(1f, 1f)
            }
            it += Graphic(entitySprite, 0)
        }
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(3f, 2f)
                setSize(1f, 1f)
                setFlip(true, false)
            }
            it += Graphic(entitySprite, 0)
        }

        // animation entities
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(2f, 1f)
                setSize(1f, 1f)
            }
            it += Tiled(GameObject.FROG, 0)
            it += Graphic(entitySprite, 0)
            it += Animation(animationService.gdxAnimation(GameObject.FROG, AnimationType.IDLE), type = NORMAL_ANIMATION)
        }
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(2f, 2f)
                setSize(1f, 1f)
                setFlip(true, false)
            }
            it += Tiled(GameObject.FROG, 0)
            it += Graphic(entitySprite, 0)
            it += Animation(animationService.gdxAnimation(GameObject.FROG, AnimationType.IDLE), type = NORMAL_ANIMATION)
        }
    }

    private fun textureRegion(key: String): TextureRegion {
        val atlas = assets[TextureAtlasAsset.GAMEOBJECT]
        return atlas.findRegion(key, 0)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        batch.use(viewport.camera.combined) {
            frogSprite.draw(it)
            frogSpriteFlipped.draw(it)
        }
        world.update(delta)
    }

    override fun dispose() {
        batch.dispose()
    }

}
