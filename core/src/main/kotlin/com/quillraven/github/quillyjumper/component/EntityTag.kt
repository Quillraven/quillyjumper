package com.quillraven.github.quillyjumper.component

import com.github.quillraven.fleks.EntityTags
import com.github.quillraven.fleks.entityTagOf

enum class EntityTag : EntityTags by entityTagOf() {
    PLAYER, CAMERA_FOCUS, BACKGROUND, FOREGROUND, COLLECTABLE
}
