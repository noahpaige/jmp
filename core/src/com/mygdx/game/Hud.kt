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

    private val pixBuffer = 10


    init {
        val skin = Skin(Gdx.files.internal("neon/skin/neon-ui.json"), TextureAtlas("neon/skin/neon-ui.atlas"))
        buildMainMenuStage(skin)
        buildPauseMenuStage(skin)
        buildDeadStage(skin)
        buildRunningStage(skin)

        Gdx.input.inputProcessor = MainMenuInputAdapter(cam, mainMenuStage)
    }

    fun update(deltaTime : Float) {
        when(JmpGame.gameState)
        {
            GameState.MainMenu ->
            {
                Gdx.input.inputProcessor = MainMenuInputAdapter(cam, mainMenuStage)
                updateMainMenuStage(deltaTime)
            }
            GameState.Paused   ->
            {
                Gdx.input.inputProcessor = PauseMenuInputAdapter(cam, pauseMenuStage)
                updatePauseMenuStage(deltaTime)
            }
            GameState.Dead     ->
            {
                Gdx.input.inputProcessor = DeadInputAdapter(cam, deadStage)
                updateDeadStage(deltaTime)
            }
            GameState.Running  ->
            {
                Gdx.input.inputProcessor = RunningInputAdapter(cam, runningStage)
                updateRunningStage(deltaTime)
            }
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
        val button = TextButton("PLAY", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2.0f - width  / 2f,
                        Gdx.graphics.height / 3.0f - height / 2f)
            label.setFontScale(2f)
        }
        val field = Label("JMP GAME", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2f      - width  / 2f,
                        Gdx.graphics.height / 3f * 2f - height / 2f)
            setFontScale(2f)
        }
        mainMenuStage.addActor(field)
        mainMenuStage.addActor(button)

    }
    private fun buildPauseMenuStage(skin : Skin) {
        val button = TextButton("RESUME", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2.0f - width / 2f,
                    Gdx.graphics.height / 3.0f - height)
            label.setFontScale(2f)
        }
        val field = Label("GAME PAUSED", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2f      - width  / 2f,
                        Gdx.graphics.height / 3f * 2f - height / 2f)
            setFontScale(2f)
        }
        pauseMenuStage.addActor(field)
        pauseMenuStage.addActor(button)

    }
    private fun buildDeadStage(skin : Skin) {
        val button = TextButton("PLAY AGAIN", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2.0f - width  / 2f,
                        Gdx.graphics.height / 3.0f - height / 2f)
            label.setFontScale(2f)
        }
        val field = Label("GAME OVER", skin).apply {
            width  = 400f
            height = 200f
            setPosition(Gdx.graphics.width  / 2f      - width  / 2f,
                        Gdx.graphics.height / 4f * 5f - height / 2f)
            setFontScale(2f)
        }
        deadStage.addActor(field)
        deadStage.addActor(button)

    }

    private fun buildRunningStage(skin : Skin) {
        val button = TextButton("| |", skin).apply {
            width = 100f
            height = 100f
            setPosition(Gdx.graphics.width  - width  - pixBuffer,
                        Gdx.graphics.height - height - pixBuffer)
            label.setFontScale(2f)
        }
        val field = Label("0", skin).apply {
            width = 200f
            height = 50f
            setPosition(Gdx.graphics.width  / 2f - width  / 2f - pixBuffer,
                        Gdx.graphics.height      - height      - pixBuffer)
            setFontScale(4f)
        }
        runningStage.addActor(field)
        runningStage.addActor(button)
    }

    private fun updateMainMenuStage(deltaTime: Float) {
        mainMenuStage.act(deltaTime)

    }
    private fun updatePauseMenuStage(deltaTime: Float) {
        pauseMenuStage.act(deltaTime)

    }
    private fun updateDeadStage(deltaTime: Float) {
        deadStage.act(deltaTime)

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