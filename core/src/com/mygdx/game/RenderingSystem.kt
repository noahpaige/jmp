package com.mygdx.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.google.inject.Inject
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold


class RenderingSystem @Inject constructor(private val batch: SpriteBatch,
                                          private val camera: OrthographicCamera) :
        IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()){
    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.transform.position
        val data = entity.physics.body.userData
        if(data is EntityData)
        entity.tryGet(TextureRegionComponent)?.let {textureRegionComponent ->
            val img = textureRegionComponent.textureRegion
            val width = img.regionWidth.pixelsToMeters
            val height = img.regionHeight.pixelsToMeters
            val scale = entity.transform.scale
            //println("Drawing a TextureRegion!")
            //batch.setColor(data.color.r, data.color.g, data.color.b, data.color.a)
            batch.draw(img,
                    position.x - width  / 2, position.y - height / 2,
                    width / 2f,
                    height / 2f,
                    width, height,
                    scale, scale,
                    entity.transform.angleRadian.toDegrees)
        }

//        entity.tryGet(SpriteComponent)?.let {spriteComponent ->
//            val img = spriteComponent.sprite.texture
//            val width = img.width.pixelsToMeters
//            val height = img.height.pixelsToMeters
//            val scale = entity.transform.scale
//            println("Drawing a Sprite!")
//            batch.draw(img,
//                    position.x - width  / 2, position.y - height / 2,
//                    width / 2f,
//                    height / 2f,
//                    width, height,
//                    scale, scale,
//                    entity.transform.angleRadian.toDegrees)
//        }

        entity.tryGet(TextureComponent)?.let {textureComponent ->
            val img = textureComponent.texture
            val data = entity.physics.body.userData
            if(data is EntityData) {
                batch.setColor(data.color.r, data.color.g, data.color.b, data.color.a)


                if(data.tag == "block") {
                    batch.draw(img,
                            position.x - img.width.pixelsToMeters / 4.0f,
                            position.y - img.height.pixelsToMeters / 4.0f,
                            img.width.pixelsToMeters / 2f, img.height.pixelsToMeters / 2f)
                }
                else if(data.tag == "floor")
                {
                    batch.draw(img,
                            position.x - 50f,
                            position.y - 0.5f,
                            100f, 1f)
                }
                else{
                    batch.draw(img,
                            position.x - img.width.pixelsToMeters / 2.0f,
                            position.y - img.height.pixelsToMeters / 2.0f,
                            img.width.pixelsToMeters, img.height.pixelsToMeters)
                }
            }
        }
    }
}


val Float.toDegrees : Float
    get() = MathUtils.radiansToDegrees * this