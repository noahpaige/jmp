package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.Viewport
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table

class Hud @Inject constructor(
        spriteBatch : SpriteBatch,
        private @GuiCam val cam : OrthographicCamera)
{
    private val viewport: Viewport = FitViewport(cam.viewportWidth, cam.viewportHeight, cam)
    val stage = Stage(viewport, spriteBatch)

    init {
        //val skin = Skin(FileHandle("skins/uiskin.json"))
        val skin = Skin(Gdx.files.internal("neon/skin/neon-ui.json"), TextureAtlas("neon/skin/neon-ui.atlas"))
        val button = TextButton("FOOOOOOK", skin, "default")
        button.width = 200f;
        button.height = 20f;
        button.setPosition(10f,10f)
        //stage.addActor(table)
        stage.addActor(button)
    }

    fun update(deltaTime : Float) {
        stage.act(deltaTime)
    }

    fun draw(){
        stage.draw()
    }
}

class HudSystem @Inject constructor(private val hud: Hud) : EntitySystem() {
    override fun update(deltaTime: Float) {
        hud.update(deltaTime)
        hud.draw()
    }
}