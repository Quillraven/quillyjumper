import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.github.quillyjumper.ParallaxBackground
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class ParallaxTestScreen : KtxScreen {
    private val spriteBatch: Batch = SpriteBatch()
    private val viewport = FitViewport(16f, 9f)
    private val parallaxBgd = ParallaxBackground(viewport)

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        viewport.apply()
        spriteBatch.use(viewport.camera) {
            parallaxBgd.scrollBy(0.01f, 0.01f)
            parallaxBgd.draw(0f, 0f, it)
        }
    }

    override fun dispose() {
        spriteBatch.disposeSafely()
        parallaxBgd.disposeSafely()
    }
}
