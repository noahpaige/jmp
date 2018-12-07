package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.google.inject.Inject


class MainMenuInputAdapter @Inject constructor(private val camera : OrthographicCamera,
                                               private val stage  : Stage) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        for (actor in stage.actors)
        {
            if (actor is TextButton)
            {
                if (actor.isHit(screenX,
                                Gdx.graphics.height - screenY))
                {
                    JmpGame.gameState = GameState.Running
                    return true
                }
            }
        }
        return true
    }
}

class PauseMenuInputAdapter @Inject constructor(private val camera : OrthographicCamera,
                                                private val stage  : Stage) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        for (actor in stage.actors)
        {
            if (actor is TextButton)
            {
                if (actor.isHit(screenX,
                                Gdx.graphics.height - screenY))
                {
                    JmpGame.gameState = GameState.Running
                    return true
                }
            }
        }
        return true
    }
}

class DeadInputAdapter @Inject constructor(private val camera : OrthographicCamera,
                                           private val stage  : Stage) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        for (actor in stage.actors)
        {
            if (actor is TextButton)
            {
                if (actor.isHit(screenX,
                                Gdx.graphics.height - screenY))
                {
                    JmpGame.gameState = GameState.Running
                    JmpGame.resetGame = true
                    return true
                }
            }
        }
        return true
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
                if (actor.isHit(screenX,Gdx.graphics.height - screenY))
                {
                    JmpGame.gameState = GameState.Paused
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