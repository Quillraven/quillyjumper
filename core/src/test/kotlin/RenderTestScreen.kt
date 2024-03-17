import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameObject
import com.quillraven.github.quillyjumper.TextureAtlasAsset
import com.quillraven.github.quillyjumper.component.*
import com.quillraven.github.quillyjumper.system.AnimationSystem
import com.quillraven.github.quillyjumper.system.RenderSystem
import ktx.app.KtxScreen
import ktx.graphics.use

class RenderTestScreen : KtxScreen {

    private val batch: Batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(16f, 9f, camera)
    private val assets = Assets()
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
            it += Graphic(entitySprite)
        }
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(3f, 2f)
                setSize(1f, 1f)
                setFlip(true, false)
            }
            it += Graphic(entitySprite)
        }

        // animation entities
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(2f, 1f)
                setSize(1f, 1f)
            }
            it += Tiled(GameObject.FROG, 0)
            it += Graphic(entitySprite)
            it += Animation(gdxAnimation(world, GameObject.FROG, AnimationType.IDLE))
        }
        world.entity {
            val entitySprite = Sprite(textureRegion("frog/idle")).apply {
                setPosition(2f, 2f)
                setSize(1f, 1f)
                setFlip(true, false)
            }
            it += Tiled(GameObject.FROG, 0)
            it += Graphic(entitySprite)
            it += Animation(gdxAnimation(world, GameObject.FROG, AnimationType.IDLE))
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
