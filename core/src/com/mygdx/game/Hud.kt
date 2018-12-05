package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.Viewport
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener



class Hud @Inject constructor(
        spriteBatch : SpriteBatch,
        private @GuiCam val cam : OrthographicCamera)
{
    private val viewport: Viewport = FitViewport(cam.viewportWidth, cam.viewportHeight, cam)
    private val stage = Stage(viewport, spriteBatch)


    init {
        val processor = Gdx.input.inputProcessor
        if(processor is InputMultiplexer) {
            println("Setting input processor to stage")
            processor.addProcessor(stage)
        }
        val skin = Skin(Gdx.files.internal("neon/skin/neon-ui.json"), TextureAtlas("neon/skin/neon-ui.atlas"))

        val table = Table(skin).apply {
            val button = TextButton("ButtonText", skin)
            val addListener: Boolean = button.addListener(object : ClickListener() {


                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    println("BOOOOOTON")
                    button.setText("You clicked the button")
                    val data = JmpGame.playerBody.userData
                    if(data is EntityData){
                        if(!data.objsInContact.isEmpty()){
                            JmpGame.playerBody.linearVelocity = Vector2(JmpGame.playerBody.linearVelocity.x,0f)
                            JmpGame.playerBody.applyLinearImpulse(Vector2(0f, Constants.PLAYER_VERTICAL_FORCE_FACTOR),
                                    JmpGame.playerBody.transform.position,
                                    true)
                        }
                    }
                }

            })
            add(button).width(200f).height(100f)
            //centerOnScreen()
            row()
            add(TextField("1234", skin)).width(200f).height(100f)
            moveToTopRight()
        }
        //table.setPosition(Constants.SCREEN_MIDDLE_X, Constants.SCREEN_MIDDLE_Y)
//        table.add(button)
//        stage.addActor(table)
        stage.addActor(table)
    }

    fun update(deltaTime : Float) {
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