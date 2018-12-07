package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton

class GameModule(private val myGdxGame: JmpGame) : Module {
    override fun configure(binder: Binder) {
        binder.bind(SpriteBatch::class.java).toInstance(myGdxGame.batch)
    }

    @Provides
    @Singleton
    fun systems() : Systems {
        return Systems(listOf(
                PhysicsSystem::class.java,
                BoxSpawnSystem::class.java,
                PlayerMovementSystem::class.java,
                PhysicsSynchronizationSystem::class.java,
                CamUpdateSystem::class.java,
                BackgroundSystem::class.java,
                RenderingSystem::class.java,
                //PhysicsDebugSystem::class.java,
                HudSystem::class.java
        ))
    }

    @Provides
    @Singleton
    fun camera() : OrthographicCamera {
        // Get the width of the screen,
        val viewportWidth  = Gdx.graphics.width.pixelsToMeters
        val viewportHeight = Gdx.graphics.height.pixelsToMeters
        return OrthographicCamera(viewportWidth, viewportHeight).apply{
            position.set(viewportWidth / 2F, viewportHeight / 2F, 0F) // 3D vector, but z is always zero
            update()
        }
    }

    @GuiCam
    @Provides
    @Singleton
    fun guiCam(): OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false)
    }

    //@WorldCam
    @Provides
    @Singleton
    fun world() : World {
        Box2D.init()
        return World(Vector2(0F, -9.81F), true)
    }
}