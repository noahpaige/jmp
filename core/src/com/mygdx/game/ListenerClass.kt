package com.mygdx.game

import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.google.inject.Inject
import javax.swing.Box


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
                entityDataA.objsInContact.remove(bodyB)
                if(isPlayerHeadBumped(bodyA, bodyB) && entityDataA.isStanding)
                {
                    println("PLAYER DIED")
                }
            }
            else if (entityDataB.tag != "player")
            {
                entityDataA.objsInContact.remove(bodyA)
                if(isPlayerHeadBumped(bodyB, bodyA) && entityDataB.isStanding)
                {
                    println("PLAYER DIED")
                }
            }
        }
    }


    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        println("Collision")
        val entityDataA = bodyA.userData
        val entityDataB = bodyB.userData

        if(entityDataA is EntityData && entityDataB is EntityData)
        {
            if(entityDataA.tag == "player")
            {
                entityDataA.objsInContact.add(bodyB)
                entityDataA.isStanding = isPlayerStanding(bodyA, bodyB)  || entityDataA.isStanding
                if(isPlayerHeadBumped(bodyA, bodyB) && entityDataA.isStanding)
                {
                    println("PLAYER DIED")
                }
            }
            else if(entityDataB.tag != "player")
            {
                entityDataA.objsInContact.add(bodyA)
                entityDataB.isStanding = isPlayerStanding(bodyB, bodyA) || entityDataB.isStanding
                if(isPlayerHeadBumped(bodyB, bodyA) && entityDataB.isStanding)
                {
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

fun isPlayerStanding(player : Body, other : Body) : Boolean
{
    val pdata = player.userData
    val odata = other.userData
    if(odata is EntityData)
    {
        if(odata.tag == "floor"){ return true }
        val pradius = player.fixtureList[0].shape.radius
        val oradius = other.fixtureList[0].shape.radius
        if(other.position.y < player.position.y)
        {
            if(player.position.x - pradius < other.position.x + oradius &&
                    player.position.x + pradius > other.position.x - oradius)
            {
                return true
            }
        }
    }
    return false
}

fun isPlayerHeadBumped(player : Body, other : Body) : Boolean
{
    val pdata = player.userData
    val odata = other.userData
    if(odata is EntityData)
    {
        val pradius = player.fixtureList[0].shape.radius
        val oradius = other.fixtureList[0].shape.radius
        if(other.position.y > player.position.y)
        {
            if(player.position.x - pradius < other.position.x + oradius &&
                    player.position.x + pradius > other.position.x - oradius)
            {
                return true
            }
        }
    }
    return false
}