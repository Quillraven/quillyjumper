import ktx.app.KtxGame
import ktx.app.KtxScreen

class JumpTestApp(val screenFactory: () -> KtxScreen) : KtxGame<KtxScreen>() {

    override fun create() {
        val testScreen = screenFactory()
        addScreen(testScreen)
        setScreen<KtxScreen>()
    }

}
