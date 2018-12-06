package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.scenes.scene2d.ui.*
import kotlin.math.max




class Hud @Inject constructor(
        spriteBatch : SpriteBatch,
        private @GuiCam val cam : OrthographicCamera)
{
    private val viewport: Viewport = FitViewport(cam.viewportWidth, cam.viewportHeight, cam)
    private val stage = Stage(viewport, spriteBatch)
    lateinit var buttons : List<TextButton>


    init {

        val skin = Skin(Gdx.files.internal("neon/skin/neon-ui.json"), TextureAtlas("neon/skin/neon-ui.atlas"))

//        val table = Table(skin).apply {
//            val button = TextButton("ButtonText", skin)
//            buttons = listOf(button)
//            for(b in buttons) {
//                add(b).width(200f).height(100f)
//                row()
//            }
//            add(TextField("1234", skin)).width(200f).height(100f)
//            moveToTopRight()
//        }

        val button = TextButton("ButtonText", skin).apply {
            width = 200f
            height = 100f
            setPosition(Gdx.graphics.width - 210f, Gdx.graphics.height - 110f)
        }
        buttons = listOf(button)
        val field = Label("0", skin).apply {
            //style =
            width = 200f
            height = 50f
            setPosition(Gdx.graphics.width / 2f - 100f, Gdx.graphics.height - 60f)
            setFontScale(2f)
        }
        stage.addActor(field)
        stage.addActor(button)
        //stage.addActor(table)

        class MyInputAdapter @Inject constructor(private val camera: OrthographicCamera) : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val worldPosition = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(),0f))

                for (actor in stage.actors)
                {
                    if (actor is TextButton)
                    {
                        if (actor.isHit(screenX,
                                        Gdx.graphics.height - screenY))
                        {
                            println("GOTEE")
                            actor.setText("GOTEE")
                            return true
                        }
                    }
                }

                val data = JmpGame.playerBody.userData
                if(data is EntityData){
                    if(!data.objsInContact.isEmpty()){
                        JmpGame.playerBody.linearVelocity = Vector2(JmpGame.playerBody.linearVelocity.x,0f)
                        JmpGame.playerBody.applyLinearImpulse(Vector2(0f, Constants.PLAYER_VERTICAL_FORCE_FACTOR),
                                JmpGame.playerBody.transform.position,
                                true)
                    }
                }

                return true
            }



        }
        Gdx.input.inputProcessor = MyInputAdapter(cam)
    }

    fun update(deltaTime : Float) {
        for (actor in stage.actors)
        {
            if(actor is Label)
                actor.setText(JmpGame.maxHeightReached.toString()) //text = JmpGame.maxHeightReached.toString()
        }
        stage.act(deltaTime)
    }

    fun draw(){
        stage.draw()
    }

    fun getStage() : Stage {
        return stage
    }

}

class HudSystem @Inject constructor(private val hud: Hud) : EntitySystem() {
    override fun update(deltaTime: Float) {
        hud.update(deltaTime)
        hud.draw()
    }
}

fun Actor.centerOnScreen() {
    setPosition((Gdx.graphics.width - width) / 2.0F, (Gdx.graphics.height - height) / 2.0F)
}

fun Actor.moveToTopRight() {
    setPosition(Gdx.graphics.width - width - 100, Gdx.graphics.height - height - 100)
}

fun Actor.isHit(x : Int, y : Int): Boolean {
    println("Hit x: " + x + " y: " + y)
    println("Btn x: " + getX() + " y: " + getY())
    return (x > getX() &&
            x < getX() + width &&
            y > getY() &&
            y < getY() + height)
}