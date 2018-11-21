package com.mygdx.game

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body


class TransformComponent(val position : Vector2,
                         var angleRadian: Float,
                         var scale: Float) : Component {
    constructor(position: Vector2) : this(position, 0F, 1F)
    companion object : ComponentResolver<TransformComponent>(TransformComponent::class.java)
}
val Entity.transform : TransformComponent
    get() = TransformComponent[this]


class TextureComponent(val texture : Texture) : Component {
    companion object : ComponentResolver<TextureComponent>(TextureComponent::class.java)
}
val Entity.texture : TextureComponent
    get() = TextureComponent[this]



class TextureRegionComponent(val textureRegion : TextureRegion) : Component {
    companion object : ComponentResolver<TextureRegionComponent>(TextureRegionComponent::class.java)
}
val Entity.textureRegion : TextureRegionComponent
    get() = TextureRegionComponent[this]

class SpriteComponent(val sprite : Sprite) : Component {
    companion object : ComponentResolver<SpriteComponent>(SpriteComponent::class.java)
}
val Entity.sprite : SpriteComponent
    get() = SpriteComponent[this]



class PhysicsComponent(val body : Body) : Component {
    companion object : ComponentResolver<PhysicsComponent>(PhysicsComponent::class.java)
}
val Entity.physics : PhysicsComponent
    get() = PhysicsComponent[this]



open class ComponentResolver<T : Component>(componentClass: Class<T>){
    val MAPPER = ComponentMapper.getFor(componentClass)
    operator fun get(entity: Entity) = MAPPER.get(entity)
}

fun <T : Component> Entity. tryGet(componentResolver: ComponentResolver<T>): T? {
    //println("Getting " + componentResolver.MAPPER.get(this))
    return componentResolver.MAPPER.get(this)
}