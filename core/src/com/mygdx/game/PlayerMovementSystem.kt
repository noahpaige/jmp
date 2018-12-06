package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import kotlin.math.max

class PlayerMovementSystem : EntitySystem() {

    val maxSpeed = 100f
    override fun update(deltaTime: Float) {
        val accelX = Gdx.input.accelerometerX
        //println("accelX: " + accelX)
        JmpGame.playerBody.apply{
            applyLinearImpulse(Vector2(-accelX, 0F), JmpGame.playerBody.transform.position, true)

            val curSpeed = Vector2.len(linearVelocity.x, linearVelocity.y)
            if(curSpeed > maxSpeed)
            {
                linearVelocity = linearVelocity.nor()
                linearVelocity.apply {
                    x *= maxSpeed
                    y *= maxSpeed
                }

            }

            if(position.x > Gdx.graphics.width.pixelsToMeters)
                setTransform(0f, position.y, 0f)
            //position.x = 0f
            else if(position.x < 0)
                setTransform(Gdx.graphics.width.pixelsToMeters, position.y, 0f)
            //position.x = Gdx.graphics.width.pixelsToMeters
        }

        JmpGame.maxHeightReached = max(JmpGame.playerBody.position.y, JmpGame.maxHeightReached);

        //println("POS: " + JmpGame.playerBody.position)
    }
}