package com.mygdx.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem


class PhysicsSynchronizationSystem : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.physics.body
        val data = body.userData
        //if(data is EntityData) if(data.tag == "block") println("SYNCING " + data.tag)
        entity.transform.position.set(body.position)
        entity.transform.angleRadian = body.angle
    }
}