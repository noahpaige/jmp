package com.mygdx.game

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.*


class JmpGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    private lateinit var img: Texture
    private val engine = Engine()
    private lateinit var injector: Injector

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
        injector = Guice.createInjector(GameModule(myGdxGame = this))
        injector.getInstance(Systems::class.java).list.map {
            injector.getInstance(it)}.forEach{ system -> engine.addSystem(system)}
        createEntities()
    }

    private fun createEntities(){
        val world = injector.getInstance(World::class.java)
        engine.addEntity(Entity().apply{
            add(TextureComponent(img))
            add(TransformComponent(Vector2(12.5F,20F)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToMeters / 2.0F, img.height.pixelsToMeters / 2.0F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))

        })
    }


    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}

class PhysicsSynchronizationSystem : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.transform.position.set(entity.physics.body.position)
    }
}

class PhysicsSystem @Inject constructor(private val world: World) :EntitySystem() {
    private var accumulator = 0f
    override fun update(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25F)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, VELOCITY_POSITIONS)
            accumulator -= TIME_STEP
        }
    }
    companion object {
        private val TIME_STEP = 1.0F / 300.0F
        private val VELOCITY_ITERATIONS = 6
        private val VELOCITY_POSITIONS = 2
    }
}

class PhysicsDebugSystem @Inject constructor(private val world: World,
                                             private val camera: OrthographicCamera) : EntitySystem() {
    private val renderer = Box2DDebugRenderer()
    override fun update(deltaTime: Float) {
        renderer.render(world, camera.combined)
    }
}


class RenderingSystem @Inject constructor(private val batch: SpriteBatch,
                                          private val camera: OrthographicCamera) : IteratingSystem(Family.all(TransformComponent::class.java, TextureComponent::class.java).get()){
    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val img = TextureComponent[entity].texture
        val position = TransformComponent[entity].position
        batch.draw(img,
                position.x - img.width.pixelsToMeters / 2.0f,
                position.y - img.height.pixelsToMeters / 2.0f,
                img.width.pixelsToMeters, img.height.pixelsToMeters)
    }
}


class GameModule(private val myGdxGame: JmpGame) : Module {
    override fun configure(binder: Binder) {
        binder.bind(SpriteBatch::class.java).toInstance(myGdxGame.batch)
    }

    @Provides @Singleton
    fun systems() : Systems {
        return Systems(listOf(
                PhysicsSystem::class.java,
                PhysicsSynchronizationSystem::class.java,
                RenderingSystem::class.java,
                PhysicsDebugSystem::class.java
        ))
    }

    @Provides @Singleton
    fun camera() : OrthographicCamera {
        // Get the width of the screen,
        val viewportWidth  = Gdx.graphics.width.pixelsToMeters
        val viewportHeight = Gdx.graphics.height.pixelsToMeters
        return OrthographicCamera(viewportWidth, viewportHeight).apply{
            position.set(viewportWidth / 2F, viewportHeight / 2F, 0F) // 3D vector, but z is always zero
            update()
        }
    }

    @Provides @Singleton
    fun world() : World {
        Box2D.init()
        return World(Vector2(0F, -9.81F), true)
    }
}

val Int.pixelsToMeters: Float
    get() = this / Constants.PIXELS_PER_METER

data class Systems(val list: List<Class<out EntitySystem>>)