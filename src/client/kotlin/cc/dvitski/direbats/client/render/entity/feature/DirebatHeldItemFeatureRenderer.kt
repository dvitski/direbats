package cc.dvitski.direbats.client.render.entity.feature

import cc.dvitski.direbats.client.render.entity.model.DirebatEntityModel
import cc.dvitski.direbats.client.render.entity.state.DirebatEntityRenderState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture

/**
 * Represents the renderer for a Direbat's held item.
 */
@Environment(EnvType.CLIENT)
class DirebatHeldItemFeatureRenderer(context: RenderLayerParent<DirebatEntityRenderState, DirebatEntityModel>) : RenderLayer<DirebatEntityRenderState, DirebatEntityModel>(context) {
    override fun submit(
        matrices: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: DirebatEntityRenderState,
        yRot: Float,
        xRot: Float
    ) {
        matrices.pushPose()

        parentModel.head.run {
            matrices.translate(x / 16.0f, y / 16.0f, z / 16.0f)
        }

        matrices.mulPose(Axis.YP.rotationDegrees(state.bodyRot))
        matrices.mulPose(Axis.XP.rotationDegrees(state.xRot))
        matrices.translate(0.0, 0.0, -0.5)

        matrices.mulPose(Axis.XP.rotationDegrees(90.0f))

        state.heldItem.submit(matrices, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor)

        matrices.popPose()
    }
}
