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
    private val viewport : Viewport = FitViewport(cam.viewportWidth, cam.viewportHeight, cam)
    private val mainMenuStage  = Stage(viewport, spriteBatch)
    private val pauseMenuStage = Stage(viewport, spriteBatch)
    private val deadStage      = Stage(viewport, spriteBatch)
    private val runningStage   = Stage(viewport, spriteBatch)


    init {
        val skin = Skin(Gdx.files.internal("neon/skin/neon-ui.json"), TextureAtlas("neon/skin/neon-ui.atlas"))
        buildMainMenuStage(skin)
        buildPauseMenuStage(skin)
        buildDeadStage(skin)
        buildRunningStage(skin)

        Gdx.input.inputProcessor = RunningInputAdapter(cam, runningStage)
    }

    fun update(deltaTime : Float) {
        when(JmpGame.gameState)
        {
            GameState.MainMenu -> updateMainMenuStage(deltaTime)
            GameState.Paused   -> updatePauseMenuStage(deltaTime)
            GameState.Dead     -> updateDeadStage(deltaTime)
            GameState.Running  -> updateRunningStage(deltaTime)
        }

    }

    fun draw(){
        when(JmpGame.gameState)
        {
            GameState.MainMenu -> mainMenuStage.draw()
            GameState.Paused   -> pauseMenuStage.draw()
            GameState.Dead     -> deadStage.draw()
            GameState.Running  -> runningStage.draw()
        }
    }

    private fun buildMainMenuStage(skin : Skin) {

    }
    private fun buildPauseMenuStage(skin : Skin) {

    }
    private fun buildDeadStage(skin : Skin) {

    }

    private fun buildRunningStage(skin : Skin) {
        val button = TextButton("ButtonText", skin).apply {
            width = 200f
            height = 100f
            setPosition(Gdx.graphics.width - 210f, Gdx.graphics.height - 110f)
        }
        val field = Label("0", skin).apply {
            width = 200f
            height = 50f
            setPosition(Gdx.graphics.width / 2f - 100f, Gdx.graphics.height - 60f)
            setFontScale(2f)
        }
        runningStage.addActor(field)
        runningStage.addActor(button)
    }

    private fun updateMainMenuStage(deltaTime: Float) {

    }
    private fun updatePauseMenuStage(deltaTime: Float) {

    }
    private fun updateDeadStage(deltaTime: Float) {

    }
    private fun updateRunningStage(deltaTime: Float) {
        for (actor in runningStage.actors)
        {
            if(actor is Label)
                actor.setText(JmpGame.maxHeightReached.toString() + " M")
        }
        runningStage.act(deltaTime)

    }

}

class HudSystem @Inject constructor(private val hud: Hud) : EntitySystem() {
    override fun update(deltaTime: Float) {
        hud.update(deltaTime)
        hud.draw()
    }
}

class RunningInputAdapter @Inject constructor(private val camera : OrthographicCamera,
                                              private val stage  : Stage) : InputAdapter() {
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
        // this is a bad place for this.
        // this makes the player jump when the screen is tapped
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

fun Actor.centerOnScreen() {
    setPosition((Gdx.graphics.width - width) / 2.0F, (Gdx.graphics.height - height) / 2.0F)
}

fun Actor.moveToTopRight() {
    setPosition(Gdx.graphics.width - width - 100, Gdx.graphics.height - height - 100)
}

fun Actor.isHit(x : Int, y : Int): Boolean {
    return (x > getX() &&
            x < getX() + width &&
            y > getY() &&
            y < getY() + height)
}