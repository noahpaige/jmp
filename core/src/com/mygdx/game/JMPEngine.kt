package com.mygdx.game

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import kotlin.math.max

class JMPEngine : Engine() {
    override fun update(deltaTime: Float) {
        when(JmpGame.gameState){
            GameState.Running -> myUpdate(deltaTime)
        }

    }
    fun myUpdate(deltaTime: Float){
        super.update(deltaTime)
    }



}