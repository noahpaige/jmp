package com.mygdx.game

import com.badlogic.ashley.core.*
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.google.inject.*
import com.badlogic.gdx.graphics.Color


class JmpGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    private val engine = JMPEngine()
    private lateinit var injector: Injector
    private lateinit var world : World

    companion object {

        internal lateinit var playerImg: Texture
        internal lateinit var blockImg: Texture
        internal lateinit var backgroundImg: Texture
        internal lateinit var playerBody: Body
        internal lateinit var floorBody: Body
        internal var maxHeightReached = 0f
        internal var gameState = GameState.MainMenu
        internal var resetGame = false
    }

    override fun create() {
        batch = SpriteBatch()
        playerImg = Texture("itsame.png")
        blockImg = Texture("faded7.png")
        backgroundImg = Texture("neonPattern2.jpg")
        backgroundImg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        injector = Guice.createInjector(GameModule(myGdxGame = this))
        //val multiplexer = InputMultiplexer()
        //multiplexer.addProcessor(injector.getInstance(UIInputAdapter::class.java))
        //Gdx.input.inputProcessor = multiplexer
        injector.getInstance(Systems::class.java).list.map {
            injector.getInstance(it)}.forEach{ system -> engine.addSystem(system)}

        createEntities()
    }

    private fun createEntities(){
        world = injector.getInstance(World::class.java)
        //player entity
        engine.addEntity(Entity().apply{
            //add(TextureRegionComponent(TextureRegion(img)))
            add(TextureComponent(playerImg))
            add(TransformComponent(Vector2(Gdx.graphics.width.pixelsToMeters / 2.0f,5F)))

            playerBody = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            playerBody.createFixture(PolygonShape().apply {
                setAsBox(playerImg.width.pixelsToMeters / 2.0F, playerImg.height.pixelsToMeters / 2.0F)
            }, 1.0F)
            playerBody.setTransform(transform.position, 0F)
            playerBody.isFixedRotation = true
            playerBody.userData = EntityData("player", mutableListOf<Body>(), true, Color(1f,1f,1f,1f))
            add(PhysicsComponent(playerBody))

        })
        //floor entity
        engine.addEntity(Entity().apply {
            add(TransformComponent(Vector2(Gdx.graphics.width.pixelsToMeters / 2.0f,-1f)))
            add(TextureComponent(blockImg))
            floorBody = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            floorBody.createFixture(PolygonShape().apply {
                setAsBox(100f, 1F)
            }, 1.0F)
            floorBody.setTransform(transform.position, 0F)
            add(PhysicsComponent(floorBody))
            floorBody.userData = EntityData("floor", mutableListOf<Body>(), false, Color(1f,1f,1f,1f))
            floorBody.gravityScale = 0f

        })
        val listenerClass = ListenerClass()
        world.setContactListener(listenerClass)
    }


    override fun render() {

        if(resetGame)
        {
            var bodies = Array<Body>()
            world.getBodies(bodies)
            for (i in 0 until bodies.size) {
                if (!world.isLocked)
                    world.destroyBody(bodies[i])
            }
            engine.removeAllEntities()
            createEntities()
            maxHeightReached = 0f
            engine.getSystem(CamUpdateSystem::class.java).resetCam()
            resetGame = false
        }

        //Gdx.gl.glClearColor(0.3f, 0.7f, 0.7f, 1f)
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)

    }

    override fun dispose() {
        batch.dispose()
        playerImg.dispose()
    }
}

enum class GameState { MainMenu, Paused, Dead, Running }

val Int.pixelsToMeters: Float
    get() = this.toFloat() / Constants.PIXELS_PER_METER

val Float.MetersToPixels: Int
    get() = (this * Constants.PIXELS_PER_METER).toInt()

data class Systems(val list: List<Class<out EntitySystem>>)