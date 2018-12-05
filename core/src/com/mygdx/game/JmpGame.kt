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






class JmpGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    private val engine = JMPEngine()
    private lateinit var injector: Injector

    companion object {

        internal lateinit var img: Texture
        internal lateinit var playerBody: Body
        internal lateinit var floorBody: Body
        internal var maxHeightReached = 0f;
    }

    override fun create() {
        batch = SpriteBatch()
        img = Texture("itsame.png")
        injector = Guice.createInjector(GameModule(myGdxGame = this))
        injector.getInstance(Systems::class.java).list.map {
            injector.getInstance(it)}.forEach{ system -> engine.addSystem(system)}

        createEntities()
        //use inpult multiplexor to manage inputs from UI vs game
        Gdx.input.inputProcessor = injector.getInstance(MyInputAdapter::class.java)
    }

    public fun getBatch() : SpriteBatch { return batch }

    private fun createEntities(){
        val world = injector.getInstance(World::class.java)
        //player entity
        engine.addEntity(Entity().apply{
            //add(TextureRegionComponent(TextureRegion(img)))
            add(TextureComponent(img))
            add(TransformComponent(Vector2(Constants.SCREEN_MIDDLE_X,5F)))

            playerBody = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            playerBody.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToMeters / 2.0F, img.height.pixelsToMeters / 2.0F)
            }, 1.0F)
            playerBody.setTransform(transform.position, 0F)
            playerBody.isFixedRotation = true
            playerBody.userData = EntityData("player", mutableListOf<Body>(), false)
            add(PhysicsComponent(playerBody))

        })
        //floor entity
        engine.addEntity(Entity().apply {
            add(TransformComponent(Vector2(Constants.SCREEN_MIDDLE_X,-1f)))
            floorBody = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            floorBody.createFixture(PolygonShape().apply {
                setAsBox(20F, 1F)
            }, 1.0F)
            floorBody.setTransform(transform.position, 0F)
            add(PhysicsComponent(floorBody))
            floorBody.userData = EntityData("floor", mutableListOf<Body>(), false)
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
        val data = playerBody.userData
        if(data is EntityData){
            if(!data.objsInContact.isEmpty()){
                playerBody.linearVelocity = Vector2(playerBody.linearVelocity.x,0f)
                playerBody.applyLinearImpulse(Vector2(0f, Constants.PLAYER_VERTICAL_FORCE_FACTOR),
                        playerBody.transform.position,
                        true)
            }
        }

        return true
    }

}


val Int.pixelsToMeters: Float
    get() = this.toFloat() / Constants.PIXELS_PER_METER

data class Systems(val list: List<Class<out EntitySystem>>)