package dev.andante.direbats.client.render.entity.state

import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState

class DirebatEntityRenderState : ItemHolderEntityRenderState() {
    var hanging: Boolean = false
    var isAttacking: Boolean = false
}
