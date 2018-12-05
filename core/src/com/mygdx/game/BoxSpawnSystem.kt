package com.mygdx.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import java.util.Random


class BoxSpawnSystem @Inject constructor(private val camera: OrthographicCamera,
                                         internal val engine: Engine,
                                         private val world: World) : EntitySystem() {
    private var counter = 0f
    private val maxX = 25f
    private val minX = 0f
    override fun update(deltaTime: Float) {
        if(counter > 2) {
            counter = 0f
            val pos = Vector2(Random().nextFloat() * (maxX - minX) + minX, camera.position.y + 30f)
            engine.addEntity(Entity().apply{
                add(TextureRegionComponent(TextureRegion(JmpGame.img)))
                //add(TextureComponent(JmpGame.img))
                add(TransformComponent(Vector2(pos.x, pos.y), 0F, 0.25F))

                val body = world.createBody(BodyDef().apply {
                    type = BodyDef.BodyType.DynamicBody
                })
                body.createFixture(PolygonShape().apply {
                    setAsBox(2F, 2F)
                }, 100.0f)
                body.setTransform(transform.position, 0F)
                body.isFixedRotation = true
                add(PhysicsComponent(body))
                body.userData = EntityData("block", mutableListOf<Body>(), false)
                body.gravityScale = 0.2f

                //body.setLinearVelocity(0f, -10f)
                //body.userData = EntityData("block")
            })
            println("SPAWNED BOX AT " + pos.x + "    " + pos.y)
        }
        else{
            counter += deltaTime
        }
    }
}
