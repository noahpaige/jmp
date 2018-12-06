package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject

class PhysicsSystem @Inject constructor(private val world: World) : EntitySystem() {
    private var accumulator = 0f
    override fun update(deltaTime: Float) {
        var adjustedDeltaTime = deltaTime
        if (JmpGame.gameState != GameState.Running) adjustedDeltaTime = 0f

        val frameTime = Math.min(adjustedDeltaTime, 0.25F)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, VELOCITY_POSITIONS)
            accumulator -= TIME_STEP
        }
    }
    companion object {
        private val TIME_STEP = 1.0F / 300.0F
        private val VELOCITY_ITERATIONS = 10
        private val VELOCITY_POSITIONS = 8
    }
}