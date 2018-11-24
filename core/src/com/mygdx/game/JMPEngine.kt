package com.mygdx.game

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

class JMPEngine : Engine() {
    override fun update(deltaTime: Float) {
        val accelX = Gdx.input.accelerometerX
        //println("accelX: " + accelX)
        JmpGame.playerBody.apply{
            val adjustedAccelX = accelX * -Constants.PLAYER_HORIZONTAL_FORCE_FACTOR
            //applyForceToCenter(Vector2(accelX * -Constants.PLAYER_HORIZONTAL_FORCE_FACTOR, 0f), true)
            applyLinearImpulse(Vector2(-accelX, 0F), JmpGame.playerBody.transform.position, true)
        }
        super.update(deltaTime)

    }

}