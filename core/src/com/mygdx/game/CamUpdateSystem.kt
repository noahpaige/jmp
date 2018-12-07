package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.google.inject.Inject
import com.mygdx.game.JmpGame.Companion.playerBody

class CamUpdateSystem @Inject constructor(private val camera: OrthographicCamera) : EntitySystem() {
    private var desiredCamPos = camera.position
    override fun update(deltaTime: Float) {
        var adjustedDeltaTime = deltaTime
        if (JmpGame.gameState != GameState.Running) adjustedDeltaTime = 0f


        if(JmpGame.playerBody.position.y > camera.position.y)
        {
            desiredCamPos.y += playerBody.position.y - camera.position.y + 10.0f * adjustedDeltaTime
        }

        camera.position.lerp(desiredCamPos, 0.01f)

        camera.update()

    }

    fun resetCam()
    {
        camera.position.y = Gdx.graphics.height.pixelsToMeters / 2f
    }

}

