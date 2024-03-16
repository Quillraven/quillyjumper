import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    Lwjgl3Application(JumpTestApp(::JumpTestScreen), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Quilly Jumper")
        setWindowedMode(640, 480)
    })
}
