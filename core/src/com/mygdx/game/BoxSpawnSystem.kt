package com.mygdx.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import java.util.Random
import kotlin.math.abs


class BoxSpawnSystem @Inject constructor(private val camera: OrthographicCamera,
                                         private val world: World) : EntitySystem() {
    private var counter = 0f
    private var spawnPoints = mutableListOf<Vector2>()

    init {
        for(i in 0..4){
            addPoint(spawnPoints, 0f)
        }
    }
    override fun update(deltaTime: Float) {
        if(counter > 2) {
            counter = 0f
            val pos = spawnPoints[0]
            engine.addEntity(Entity().apply{
                //add(TextureRegionComponent(TextureRegion(JmpGame.img)))
                add(TextureComponent(JmpGame.blockImg))
                add(TransformComponent(Vector2(pos.x, pos.y), 0F, 0.25F))

                val body = world.createBody(BodyDef().apply {
                    type = BodyDef.BodyType.DynamicBody
                })
                body.createFixture(PolygonShape().apply {
                    setAsBox(Constants.BLOCK_WIDTH, Constants.BLOCK_WIDTH)
                }, 100.0f)
                body.setTransform(transform.position, 0F)
                body.isFixedRotation = true
                add(PhysicsComponent(body))

                body.userData = EntityData("block",
                        mutableListOf<Body>(),
                        false,
                        calcColor(pos.x))
                body.gravityScale = 0.2f
                body.isSleepingAllowed = false
                //body.setLinearVelocity(0f, -10f)
                //body.userData = EntityData("block")
            })
            spawnPoints.removeAt(0)
            addPoint(spawnPoints, camera.position.y)
        }
        else{
            counter += deltaTime
        }
    }
}

fun calcColor(posX : Float) : Color {
    var rfactor = (Gdx.graphics.width.pixelsToMeters - posX) / Gdx.graphics.width.pixelsToMeters
    var gfactor = rfactor * 2f
    var bfactor = rfactor * 3f

    println("rfactor: " + rfactor)

    if (rfactor >= 0.5f)
        gfactor = (gfactor - 0.5f) * 2f
    if (rfactor >= 0.333f && rfactor < 0.667f)
        bfactor = (rfactor - 0.333f) * 3f
    else if(rfactor >= 0.667f)
        bfactor = (rfactor - 0.667f) * 3f

    rfactor = rfactor * 0.7f + 0.3f
    gfactor = gfactor * 0.7f + 0.3f
    bfactor = bfactor * 0.7f + 0.3f

    return Color(rfactor, gfactor, bfactor, 1f)

}

fun getRandomColor() : Color
{
    val rand = Random()
    val max = 1.0f
    val min = 0.3f
    return Color(rand.nextFloat() * (max - min) + min,
                 rand.nextFloat() * (max - min) + min,
                 rand.nextFloat() * (max - min) + min,
                 1.0f)
}

fun addPoint(points : MutableList<Vector2>, camPosY : Float) {
    val maxX = Gdx.graphics.width.pixelsToMeters
    val minX = 0f

    val rand = Random()
    var point = Vector2(rand.nextFloat()  * (maxX - minX) + minX,
                        camPosY + Gdx.graphics.height.pixelsToMeters + 10f)
    while (checkBoxSpawnConflict(points, point.x))
        point = Vector2(rand.nextFloat()  * (maxX - minX) + minX,
                        camPosY + Gdx.graphics.height.pixelsToMeters + 10f)

    points.add(point)
}

fun checkBoxSpawnConflict(points : List<Vector2>, pointX : Float) : Boolean
{
    for (point in points)
    {
        if (abs(point.x - pointX) < Constants.BLOCK_WIDTH) return true
    }
    return false
}