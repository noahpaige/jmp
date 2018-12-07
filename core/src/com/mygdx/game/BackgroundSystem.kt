package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.inject.Inject
import javax.swing.text.Position
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BackgroundSystem @Inject constructor(private val batch: SpriteBatch,
                                           private val camera: OrthographicCamera): EntitySystem()
{
    private var position = Vector2(0f,0f)
    private val give = Vector2(50f, 50f)
    override fun update(deltaTime: Float) {


        batch.begin()

        batch.drawParallaxRepeating(JmpGame.backgroundImg,
                                    0,
                                    -camera.position.y.MetersToPixels,
                                    position,
                                    give,
                             5f,
                                    deltaTime)

//        position = calcParallaxPosition(position, give, 5f, deltaTime)
//
//        batch.draw(JmpGame.backgroundImg,
//                    position.x - give.x,
//                    position.y - give.y,
//                    Gdx.graphics.width.toFloat() + give.x * 2f,
//                    Gdx.graphics.height.toFloat() + give.y * 2f)
        batch.end()
    }
}

fun calcParallaxPosition(position : Vector2, give : Vector2, speed : Float, deltaTime: Float) : Vector2
{
    var smoothingFactor = Vector2(speed,speed)

    val accelX = Gdx.input.accelerometerX
    val accelY = Gdx.input.accelerometerY

    if(accelX.isPositive()) smoothingFactor.x = speed * (give.x + position.x) / (give.x * 2f)
    else                    smoothingFactor.x = speed * (give.x - position.x) / (give.x * 2f)

    if(accelY.isPositive()) smoothingFactor.y = speed * (give.y + position.y) / (give.y * 2f)
    else                    smoothingFactor.y = speed * (give.y - position.y) / (give.y * 2f)


    val desiredVector = Vector2(position.x - (accelX * 10f) * deltaTime * smoothingFactor.x,
            position.y - (accelY * 10f) * deltaTime * smoothingFactor.y)

    if(abs(desiredVector.x) < give.x) position.x = desiredVector.x
    if(abs(desiredVector.y) < give.y) position.y = desiredVector.y

    return position


}

fun Float.isPositive() : Boolean {
    if(this > 0f) return true
    return false
}

fun Batch.drawParallaxRepeating(texture   : Texture,
                                srcX      : Int,
                                srcY      : Int,
                                position  : Vector2,
                                give      : Vector2,
                                speed     : Float,
                                deltaTime : Float){

    var newPosition = calcParallaxPosition(position, give, speed, deltaTime)
    draw(texture,
            newPosition.x - give.x,
            newPosition.y - give.y,
            srcX,
            srcY,
            Gdx.graphics.width + give.x.toInt() * 2,
            Gdx.graphics.height+ give.y.toInt() * 2)
//    draw(texture,
//            newPosition.x - give.x,
//            newPosition.y - give.y,
//            width + give.x * 2f,
//            height + give.y * 2f)


}

fun Batch.drawParallax(texture   : Texture,
                       width     : Float,
                       height    : Float,
                       position  : Vector2,
                       give      : Vector2,
                       speed     : Float,
                       deltaTime : Float){

    var newPosition = calcParallaxPosition(position, give, 5f, deltaTime)

    draw(texture,
            newPosition.x - give.x,
            newPosition.y - give.y,
            width + give.x * 2f,
            height + give.y * 2f)


}