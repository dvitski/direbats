package cc.dvitski.direbats.client.render.entity.state

import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState

class DirebatEntityRenderState : HoldingEntityRenderState() {
    var hanging: Boolean = false
    var isAttacking: Boolean = false
}
