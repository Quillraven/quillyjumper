import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.isEqual
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.PropertiesUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.configureWorld
import com.quillraven.github.quillyjumper.Assets
import com.quillraven.github.quillyjumper.GameProperties
import com.quillraven.github.quillyjumper.audio.AudioService
import com.quillraven.github.quillyjumper.component.EntityTag
import com.quillraven.github.quillyjumper.component.Jump
import com.quillraven.github.quillyjumper.component.Jump.Companion.JUMP_BUFFER_TIME
import com.quillraven.github.quillyjumper.component.Physic
import com.quillraven.github.quillyjumper.system.JumpPhysicSystem
import com.quillraven.github.quillyjumper.system.PhysicRenderDebugSystem
import com.quillraven.github.quillyjumper.system.PhysicSystem
import com.quillraven.github.quillyjumper.toGameProperties
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.chain
import ktx.box2d.createWorld
import ktx.graphics.use
import ktx.math.vec2

class JumpTestScreen : KtxScreen, ContactListener {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(16f, 9f, gameCamera)
    private val physicWorld = createWorld(gravity = vec2(0f, -30f)).apply {
        autoClearForces = false
        setContactListener(this@JumpTestScreen)
    }
    private val assets: Assets by lazy { Assets() }
    private val gameProperties: GameProperties by lazy(::loadGameProperties)
    private val audioService: AudioService by lazy {
        AudioService(assets, soundVolume = gameProperties.soundVolume, musicVolume = gameProperties.musicVolume)
    }
    private val shapeRenderer = ShapeRenderer()
    private val world = configureWorld {
        injectables {
            add(gameCamera)
            add("gameViewport", gameViewport)
            add(physicWorld)
            add(audioService)
        }
        systems {
            add(JumpPhysicSystem())
            add(PhysicSystem(interval = Fixed(1 / 240f)))
            add(PhysicRenderDebugSystem())
        }
    }
    private lateinit var jumpEntity: Entity
    private val feetAABB = Rectangle()

    private fun loadGameProperties(): GameProperties {
        val propertiesMap = ObjectMap<String, String>()
        Gdx.files.internal("game.properties").reader().use {
            PropertiesUtils.load(propertiesMap, it)
        }
        return propertiesMap.toGameProperties()
    }

    override fun show() {
        val jumpHeight = 3f

        // character test entity
        jumpEntity = world.entity {
            it += Jump(jumpHeight, lowerFeet = vec2(-0.5f, -0.5f), upperFeet = vec2(0.5f, 0.5f))
            val body = physicWorld.body(BodyType.DynamicBody) {
                position.set(5f, 1f)
                box(1f, 1f) {
                    userData = "player"
                }
            }
            it += Physic(body)
            it += EntityTag.PLAYER
        }
        // create ground
        physicWorld.body {
            position.set(4f, 0f)
            box(6f, 1f)
        }
        // jump height verification sensor
        val tolerance = 0.05f
        physicWorld.body {
            position.set(0f, 0f)
            val vertices = arrayOf(
                Vector2(4f, 1.5f + jumpHeight - tolerance),
                Vector2(6f, 1.5f + jumpHeight - tolerance),
            )
            chain(*vertices) {
                userData = "heightSensor"
                isSensor = true
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
    }

    override fun render(delta: Float) = with(world) {
        val (body) = jumpEntity[Physic]
        if (isEqual(body.position.y, 1f, 0.05f)) {
            jumpEntity[Jump].buffer = JUMP_BUFFER_TIME
            this.system<JumpPhysicSystem>().debugFeetAABB(jumpEntity, feetAABB)
        }

        this.update(delta)

        // debug feet AABB
        shapeRenderer.use(ShapeRenderer.ShapeType.Line, gameCamera.combined) {
            it.color = Color.TEAL
            it.rect(feetAABB.x, feetAABB.y, feetAABB.width, feetAABB.height)
        }
    }

    override fun beginContact(contact: Contact) {
        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        if ((fixA.userData == "player" && fixB.userData == "heightSensor")
            || (fixB.userData == "player" && fixA.userData == "heightSensor")
        ) {
            println("collision")
        }
    }

    override fun endContact(contact: Contact) = Unit

    override fun preSolve(contact: Contact, oldManifold: Manifold) = Unit

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    override fun dispose() {
        physicWorld.disposeSafely()
        world.dispose()
        shapeRenderer.disposeSafely()
    }
}
