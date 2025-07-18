package dev.andante.direbats.client.render.entity.feature

import dev.andante.direbats.client.render.entity.model.DirebatEntityModel
import dev.andante.direbats.client.render.entity.state.DirebatEntityRenderState
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

/**
 * Represents the renderer for a Direbat's held item.
 */
@Environment(EnvType.CLIENT)
class DirebatHeldItemFeatureRenderer(context: FeatureRendererContext<DirebatEntityRenderState, DirebatEntityModel>) : FeatureRenderer<DirebatEntityRenderState, DirebatEntityModel>(context) {
    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        state: DirebatEntityRenderState,
        limbAngle: Float,
        limbDistance: Float
    ) {

        MinecraftClient.getInstance()?.run {
            matrices.push()

            contextModel.head.run {
                matrices.translate(originX / 16.0f, originY / 16.0f, originZ / 16.0f)
            }

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.bodyYaw))
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch))
            matrices.translate(0.0, 0.0, -0.5)

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f))

            state.itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV)

            matrices.pop()
        }
    }
}
