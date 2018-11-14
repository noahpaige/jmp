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
import com.google.inject.*
import com.google.inject.Guice.createInjector


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
        engine.addEntity(Entity().apply{
            add(TextureComponent(img))
            add(TransformComponent(Vector2(10F,20F)))
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
        batch.draw(img, position.x, position.y, img.width.pixelsToMeters, img.height.pixelsToMeters)
    }
}


class GameModule(private val myGdxGame: JmpGame) : Module {
    override fun configure(binder: Binder) {
        binder.bind(SpriteBatch::class.java).toInstance(myGdxGame.batch)
    }

    @Provides @Singleton
    fun systems() : Systems {
        return Systems(listOf(
                RenderingSystem::class.java
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
}

val Int.pixelsToMeters: Float
    get() = this / Constants.PIXELS_PER_METER

data class Systems(val list: List<Class<out EntitySystem>>)