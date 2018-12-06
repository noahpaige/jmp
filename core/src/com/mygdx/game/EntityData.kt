package com.mygdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.Body

class EntityData constructor(val tag : String,
                             var objsInContact: MutableList<Body>,
                             var isStanding: Boolean,
                             var color : Color)