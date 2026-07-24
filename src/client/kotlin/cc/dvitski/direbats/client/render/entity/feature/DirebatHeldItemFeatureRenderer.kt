package cc.dvitski.direbats.client.render.entity.feature

import cc.dvitski.direbats.client.render.entity.model.DirebatEntityModel
import cc.dvitski.direbats.entity.DirebatEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext

/**
 * Represents the renderer for a Direbat's held item.
 */
@Environment(EnvType.CLIENT)
class DirebatHeldItemFeatureRenderer(context: RenderLayerParent<DirebatEntity, DirebatEntityModel>) : RenderLayer<DirebatEntity, DirebatEntityModel>(context) {
    override fun render(
        matrices: PoseStack,
        vertices: MultiBufferSource,
        light: Int,
        entity: DirebatEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        matrices.pushPose()

        parentModel.head.run {
            matrices.translate(x / 16.0f, y / 16.0f, z / 16.0f)
        }

        matrices.mulPose(Axis.YP.rotationDegrees(netHeadYaw))
        matrices.mulPose(Axis.XP.rotationDegrees(headPitch))
        matrices.translate(0.0, 0.0, -0.5)

        matrices.mulPose(Axis.XP.rotationDegrees(90.0f))

        val stack = entity.getItemBySlot(EquipmentSlot.MAINHAND)
        Minecraft.getInstance().itemRenderer.renderStatic(
            entity, stack, ItemDisplayContext.GROUND, false, matrices, vertices, entity.level(),
            light, OverlayTexture.NO_OVERLAY, entity.id
        )

        matrices.popPose()
    }
}
