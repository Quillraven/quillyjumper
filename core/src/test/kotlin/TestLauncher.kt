import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    Lwjgl3Application(TestGame(::ShaderTestScreen), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Quilly Jumper Testsuite")
        setWindowedMode(640, 360)
    })
}
