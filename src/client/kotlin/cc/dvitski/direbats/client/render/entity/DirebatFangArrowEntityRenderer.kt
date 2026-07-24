package cc.dvitski.direbats.client.render.entity

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.entity.DirebatFangArrowEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.projectile.AbstractArrow

/**
 * Represents the renderer for a [DirebatFangArrowEntity].
 */
@Environment(EnvType.CLIENT)
class DirebatFangArrowEntityRenderer<T : AbstractArrow>(ctx: EntityRendererProvider.Context) : ArrowRenderer<T>(ctx) {
    override fun getTextureLocation(entity: T): ResourceLocation {
        return TEXTURE
    }

    companion object {
        val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/arrow/direbat_fang_arrow.png")
    }
}
