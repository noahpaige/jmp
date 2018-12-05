package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.mygdx.game.JmpGame.Companion.playerBody

class CamUpdateSystem @Inject constructor(private val world: World,
                                          private val camera: OrthographicCamera) : EntitySystem() {
    var desiredCamPos = camera.position;
    override fun update(deltaTime: Float) {

        if(JmpGame.playerBody.position.y > camera.position.y)
        {
            println("UPDATING CAM")
            desiredCamPos.y = playerBody.position.y + 10.0f * deltaTime;
        }

        camera.position.lerp(desiredCamPos, 0.01f);
        camera.update()

    }
}

