package cc.dvitski.direbats.client.render.entity

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.client.render.entity.feature.DirebatHeldItemFeatureRenderer
import cc.dvitski.direbats.client.render.entity.model.DirebatEntityModel
import cc.dvitski.direbats.client.render.entity.model.DirebatsEntityModelLayers
import cc.dvitski.direbats.entity.DirebatEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

/**
 * Represents the renderer for a [DirebatEntity].
 */
@Environment(EnvType.CLIENT)
class DirebatEntityRenderer(context: EntityRendererProvider.Context) : MobRenderer<DirebatEntity, DirebatEntityModel>(
    context,
    DirebatEntityModel(context.bakeLayer(DirebatsEntityModelLayers.DIREBAT)),
    0.5f
) {
    init {
        addLayer(DirebatHeldItemFeatureRenderer(this))
    }

    override fun getTextureLocation(entity: DirebatEntity): ResourceLocation {
        return if (entity.isAggressive) TEXTURE_ANGRY else TEXTURE
    }

    override fun setupRotations(
        entity: DirebatEntity,
        matrices: PoseStack,
        ageInTicks: Float,
        bodyYaw: Float,
        partialTick: Float,
        scale: Float
    ) {
        super.setupRotations(entity, matrices, ageInTicks, bodyYaw, partialTick, scale)
        if (entity.hanging) {
            matrices.mulPose(Axis.XP.rotationDegrees(180f))
            matrices.mulPose(Axis.YP.rotationDegrees(180f))
            matrices.translate(0.0, -1.0, 0.0)
        } else {
            val bob = Mth.cos(ageInTicks * 0.25f) * 0.1
            val offset = entity.getDimensions(entity.pose).height / 2.0
            matrices.translate(0.0, bob - offset, 0.0)
        }
    }

    companion object {
        val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/direbat/direbat.png")
        val TEXTURE_ANGRY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/direbat/direbat_angry.png")
    }
}
