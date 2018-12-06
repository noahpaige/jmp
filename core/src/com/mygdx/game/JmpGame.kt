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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mygdx.game.JmpGame.Companion.playerBody
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import java.awt.Button


class JmpGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    private val engine = JMPEngine()
    private lateinit var injector: Injector

    companion object {

        internal lateinit var playerImg: Texture
        internal lateinit var blockImg: Texture
        internal lateinit var playerBody: Body
        internal lateinit var floorBody: Body
        internal var maxHeightReached = 0f
        internal var gameState = GameState.Running
    }

    fun getEngine() : JMPEngine {
        return engine
    }

    override fun create() {
        batch = SpriteBatch()
        playerImg = Texture("itsame.png")
        blockImg = Texture("faded5.png")
        injector = Guice.createInjector(GameModule(myGdxGame = this))
        //val multiplexer = InputMultiplexer()
        //multiplexer.addProcessor(injector.getInstance(UIInputAdapter::class.java))
        //Gdx.input.inputProcessor = multiplexer
        injector.getInstance(Systems::class.java).list.map {
            injector.getInstance(it)}.forEach{ system -> engine.addSystem(system)}

        createEntities()
        //use inpult multiplexor to manage inputs from UI vs game
    }

    public fun getBatch() : SpriteBatch { return batch }

    private fun createEntities(){
        val world = injector.getInstance(World::class.java)
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
            playerBody.userData = EntityData("player", mutableListOf<Body>(), false, Color(1f,1f,1f,1f))
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
        val listenerClass = ListenerClass(world)
        world.setContactListener(listenerClass)
    }

    override fun render() {

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

enum class GameState { Running, Paused, Dead }

val Int.pixelsToMeters: Float
    get() = this.toFloat() / Constants.PIXELS_PER_METER

data class Systems(val list: List<Class<out EntitySystem>>)