package com.mygdx.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Inject


class ListenerClass @Inject constructor(private val world : World): ContactListener {
    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

    override fun endContact(contact: Contact){
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        val entityDataA = bodyA.userData
        val entityDataB = bodyB.userData

        if(entityDataA is EntityData && entityDataB is EntityData) {
            if (entityDataA.tag == "player")
            {
                entityDataA.isStanding = isPlayerStanding(JmpGame.playerBody)
                entityDataA.objsInContact.remove(bodyB)
                //println("Player ending contact A")
            }
            else if (entityDataB.tag == "player")
            {
                //println("Player ending contact B")
                entityDataB.isStanding = isPlayerStanding(JmpGame.playerBody)
                entityDataB.objsInContact.remove(bodyA)
            }
        }
    }


    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        //println("Collision")
        val entityDataA = bodyA.userData
        val entityDataB = bodyB.userData
        val contactPos = contact.worldManifold.points[0]
        if(entityDataA is EntityData && entityDataB is EntityData)
        {
            if(entityDataA.tag == "player")
            {
                //println("Player beginning contact A")
                entityDataA.objsInContact.add(bodyB)
                entityDataA.isStanding = isPlayerStanding(JmpGame.playerBody)
                //println("Head bumped: " + isPlayerHeadBumped(bodyA, bodyB))
                //println("standing: " + entityDataA.isStanding)
                if(isPlayerHeadBumped(bodyA, bodyB, contactPos) && entityDataA.isStanding)
                {
                    JmpGame.gameState = GameState.Dead
                    println("PLAYER DIED")
                }
            }
            else if(entityDataB.tag == "player")
            {
                //println("Player beginning contact B")
                entityDataA.objsInContact.add(bodyA)
                entityDataB.isStanding = isPlayerStanding(JmpGame.playerBody)
                //println("Head bumped: " + isPlayerHeadBumped(bodyB, bodyA))
                //println("standing: " + entityDataB.isStanding)
                if(isPlayerHeadBumped(bodyB, bodyA, contactPos) && entityDataB.isStanding)
                {
                    JmpGame.gameState = GameState.Dead
                    println("PLAYER DIED")
                }
            }
            if(entityDataA.isStanding || entityDataB.isStanding) println("STANDING")

//            if(entityDataA.tag != "player" && entityDataB.tag != "player")
//            {
//                println("satisfies IF")
//                val joint = DistanceJointDef()
//                joint.bodyA = bodyA
//                joint.bodyB = bodyB
//
//                world.createJoint(joint)
//            }
        }
    }
}

fun isPlayerStanding(player : Body) : Boolean
{
    val pdata = player.userData
    if(pdata is EntityData)
    {
        for (other in pdata.objsInContact)
        {
            val odata = other.userData
            if(odata is EntityData)
            {
                if(odata.tag == "floor"){ return true }
                val pwidth = JmpGame.img.width.pixelsToMeters / 2.0F
                val owidth = 2.0f
                if(other.position.y < player.position.y)
                {
                    if(player.position.x - pwidth < other.position.x + owidth &&
                            player.position.x + pwidth > other.position.x - owidth)
                    {
                        return true
                    }
                }
            }
        }
    }
    return false
}

fun isPlayerHeadBumped(player : Body, other : Body, pos : Vector2) : Boolean
{
    val odata = other.userData
    if(odata is EntityData)
    {
        val pwidth = JmpGame.img.width.pixelsToMeters / 2.0F
        val owidth = 2.0f
        if(other.position.y > player.position.y)
        {
//            println("position.y is greater")
//            println("playerPos: " + player.position + " Radius: " + pwidth)
//            println("otherPos: " + other.position + " Radius: " + owidth)
            if(player.position.x - pwidth < other.position.x + owidth &&
                    player.position.x + pwidth > other.position.x - owidth)
            {
                if(pos.y > player.position.y) return true
            }
        }
    }
    return false
}

