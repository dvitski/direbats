package dev.andante.direbats.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.andante.direbats.Direbats
import dev.andante.direbats.client.render.entity.feature.DirebatHeldItemFeatureRenderer
import dev.andante.direbats.client.render.entity.model.DirebatEntityModel
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelLayers
import dev.andante.direbats.client.render.entity.state.DirebatEntityRenderState
import dev.andante.direbats.entity.DirebatEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

/**
 * Represents the renderer for a [DirebatEntity].
 */
@Environment(EnvType.CLIENT)
class DirebatEntityRenderer(context: EntityRendererProvider.Context) : MobRenderer<DirebatEntity, DirebatEntityRenderState, DirebatEntityModel>(
    context,
    DirebatEntityModel(context.bakeLayer(DirebatsEntityModelLayers.DIREBAT)),
    0.5f
) {
    init {
        addLayer(DirebatHeldItemFeatureRenderer(this))
    }

    override fun createRenderState(): DirebatEntityRenderState {
        return DirebatEntityRenderState()
    }

    override fun extractRenderState(entity: DirebatEntity, state: DirebatEntityRenderState, tickDelta: Float) {
        super.extractRenderState(entity, state, tickDelta)
        HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, itemModelResolver)
        state.hanging = entity.hanging
        state.isAttacking = entity.isAggressive
    }

    override fun getTextureLocation(state: DirebatEntityRenderState): ResourceLocation {
        return if (state.isAttacking) TEXTURE_ANGRY else TEXTURE
    }

    override fun setupRotations(
        state: DirebatEntityRenderState,
        matrices: PoseStack,
        bodyYaw: Float,
        baseHeight: Float
    ) {
        super.setupRotations(state, matrices, bodyYaw, baseHeight)
        if (state.hanging) {
            matrices.mulPose(Axis.XP.rotationDegrees(180f))
            matrices.mulPose(Axis.YP.rotationDegrees(180f))
            matrices.translate(0.0, -1.0, 0.0)
        } else {
            val bob = Mth.cos(state.walkAnimationPos * 0.25f) * 0.1
            val offset = baseHeight / 2.0
            matrices.translate(0.0, bob - offset, 0.0)
        }
    }

    companion object {
        val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/direbat/direbat.png")
        val TEXTURE_ANGRY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "textures/entity/direbat/direbat_angry.png")
    }
}
