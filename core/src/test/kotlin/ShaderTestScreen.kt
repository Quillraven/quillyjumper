import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.ShaderAsset
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class ShaderTestScreen : KtxScreen {

    private val texture = Texture("graphics/object/frog.png")
    private val originalSprite = Sprite(texture)
    private val flashSprite = Sprite(texture)
    private val viewport = StretchViewport(16f, 9f)
    private val batch = SpriteBatch()
    private val assets = Assets()

    private val flashColor = Color.RED
    private var flashWeight = 0f
    private val flashShader by lazy { assets[ShaderAsset.FLASH] }

    override fun show() {
        assets.loadAll()

        originalSprite.setPosition(1f, 1f)
        originalSprite.setSize(2f, 2f)

        flashSprite.setPosition(3f, 1f)
        flashSprite.setSize(2f, 2f)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        flashWeight += delta
        if (flashWeight >= 1f) {
            flashWeight = 0f
        }

        viewport.apply()
        batch.use(viewport.camera) {
            // render original sprite
            batch.shader = null
            originalSprite.draw(it)

            // render flash sprite
            batch.shader = flashShader
            flashShader.use {
                flashShader.setUniformf("u_FlashColor", flashColor)
                flashShader.setUniformf("u_FlashWeight", flashWeight)
            }
            flashSprite.draw(it)
        }
    }

    override fun dispose() {
        texture.disposeSafely()
        batch.disposeSafely()
        assets.disposeSafely()
    }

}
