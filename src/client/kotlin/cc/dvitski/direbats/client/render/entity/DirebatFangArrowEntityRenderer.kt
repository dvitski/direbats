package cc.dvitski.direbats.client.render.entity

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.entity.DirebatFangArrowEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.projectile.arrow.AbstractArrow

/**
 * Represents the renderer for a [DirebatFangArrowEntity].
 */
@Environment(EnvType.CLIENT)
class DirebatFangArrowEntityRenderer<T : AbstractArrow>(ctx: EntityRendererProvider.Context) : ArrowRenderer<T, TippableArrowRenderState>(ctx) {
    override fun getTextureLocation(state: TippableArrowRenderState): Identifier {
        return TEXTURE
    }

    override fun createRenderState(): TippableArrowRenderState {
        return TippableArrowRenderState()
    }

    companion object {
        val TEXTURE: Identifier = Identifier.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/arrow/direbat_fang_arrow.png")
    }
}
