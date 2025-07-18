package dev.andante.direbats.client.render.entity

import dev.andante.direbats.Direbats
import dev.andante.direbats.entity.DirebatFangArrowEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.ProjectileEntityRenderer
import net.minecraft.client.render.entity.state.ArrowEntityRenderState
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.util.Identifier

/**
 * Represents the renderer for a [DirebatFangArrowEntity].
 */
@Environment(EnvType.CLIENT)
class DirebatFangArrowEntityRenderer<T : PersistentProjectileEntity>(ctx: EntityRendererFactory.Context) : ProjectileEntityRenderer<T, ArrowEntityRenderState>(ctx) {
    override fun getTexture(state: ArrowEntityRenderState): Identifier {
        return TEXTURE
    }

    override fun createRenderState(): ArrowEntityRenderState? {
        return ArrowEntityRenderState()
    }

    companion object {
        val TEXTURE: Identifier = Identifier.of(Direbats.MOD_ID, "textures/entity/arrow/direbat_fang_arrow.png")
    }
}
