package com.mygdx.game

import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.google.inject.Inject


class ListenerClass @Inject constructor(private val world : World): ContactListener {
    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

    override fun endContact(contact: Contact) = Unit

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        println("Collision")
        val entityDataA = bodyA.userData
        val entityDataB = bodyB.userData

        if(entityDataA is EntityData &&
                entityDataB is EntityData)
        {
            if(entityDataA.tag == "player")     { entityDataA.canJump = true }
            else if(entityDataB.tag != "player"){ entityDataB.canJump = true }

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