package com.mygdx.game

import com.badlogic.ashley.core.*
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
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
        //use inpult multiplexor to manage inputs from UI vs game
        Gdx.input.inputProcessor = injector.getInstance(MyInputAdapter::class.java)
    }

    private fun createEntities(){
        val world = injector.getInstance(World::class.java)
        //player entity
        engine.addEntity(Entity().apply{
            add(TextureComponent(img))
            add(TransformComponent(Vector2(Constants.SCREEN_MIDDLE_X,20F)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToMeters / 2.0F, img.height.pixelsToMeters / 2.0F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))

        })
        //floor entity
        engine.addEntity(Entity().apply {
            add(TransformComponent(Vector2(Constants.SCREEN_MIDDLE_X,0f)))
            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(20F, 1F)
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


class MyInputAdapter @Inject constructor(private val camera: OrthographicCamera,
                                         private val engine: Engine,
                                         private val world: World) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        println("ScreenX: $screenX        ScreenY: $screenY")
        val worldPosition = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(),0f))
        println("WorldX: ${worldPosition.x}        WorldY: ${worldPosition.y}")


        //
        engine.addEntity(Entity().apply{
            add(TransformComponent(Vector2(worldPosition.x, worldPosition.y)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))

        })
        //

        return true

    }
}

val Int.pixelsToMeters: Float
    get() = this / Constants.PIXELS_PER_METER

data class Systems(val list: List<Class<out EntitySystem>>)