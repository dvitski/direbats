package dev.andante.direbats.client.render.entity.model

import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.FANGS
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.LEFT_WING_OUTER
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.LEGS
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.RIGHT_WING_OUTER
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.TAILBONE
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelPartNames.TALONS
import dev.andante.direbats.client.render.entity.state.DirebatEntityRenderState
import dev.andante.direbats.entity.DirebatEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartNames.BODY
import net.minecraft.client.model.geom.PartNames.HEAD
import net.minecraft.client.model.geom.PartNames.LEFT_WING
import net.minecraft.client.model.geom.PartNames.RIGHT_WING
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth

/**
 * Represents the model for a [DirebatEntity].
 */
@Suppress("unused")
@Environment(EnvType.CLIENT)
class DirebatEntityModel(root: ModelPart) : EntityModel<DirebatEntityRenderState>(root) {
    private val body: ModelPart = root.getChild(BODY)
    private val leftWing: ModelPart = body.getChild(LEFT_WING)
    private val leftWingOuter: ModelPart = leftWing.getChild(LEFT_WING_OUTER)
    private val rightWing: ModelPart = body.getChild(RIGHT_WING)
    private val rightWingOuter: ModelPart = rightWing.getChild(RIGHT_WING_OUTER)
    private val legs: ModelPart = body.getChild(LEGS)
    private val talons: ModelPart = legs.getChild(TALONS)
    private val tailbone: ModelPart = body.getChild(TAILBONE)
    val head: ModelPart = root.getChild(HEAD)
    private val fangs: ModelPart = head.getChild(FANGS)

    override fun setupAnim(state: DirebatEntityRenderState) {
        if (state.hanging) {
            head.xRot = state.xRot * (Math.PI.toFloat() / 180f)
            body.xRot = 0.0f
            rightWing.xRot = 0.0f
            leftWing.xRot = 0.0f
            leftWing.yRot = 1.5f
            rightWing.yRot = -leftWing.yRot
            leftWingOuter.yRot = 90.0f
            rightWingOuter.yRot = -leftWingOuter.yRot
        } else {
            head.xRot = state.xRot * (Math.PI / 180).toFloat()
            head.yRot = state.yRot * (Math.PI / 180).toFloat()
            val animationProgress = state.ageInTicks
            body.xRot = (Math.PI / 4).toFloat() + Mth.cos(animationProgress * 0.1f) * 0.15f
            rightWing.yRot = Mth.cos(animationProgress * 0.4f) * Math.PI.toFloat() * 0.4f
            leftWing.yRot = -rightWing.yRot
            rightWingOuter.yRot = rightWing.yRot * 0.5f
            leftWingOuter.yRot = leftWing.yRot * 0.5f
        }
    }

    companion object {
        @Suppress("UNUSED_VARIABLE")
        val TEXTURED_MODEL_DATA: LayerDefinition
            get() {
                val data = MeshDefinition()
                val root = data.root

                val body = root.addOrReplaceChild(
                    BODY,
                    CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0f, 0.0f, -3.0f, 8.0f, 13.0f, 6.0f),
                    PartPose.offsetAndRotation(0.0f, 6.5774f, 0.9063f, 0.8727f, 0.0f, 0.0f)
                )

                val leftWing = body.addOrReplaceChild(
                    LEFT_WING,
                    CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(0.0f, 0.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    PartPose.offsetAndRotation(4.0f, 1.0f, 2.0f, 0.0f, -0.5236f, 0.0f)
                )

                val leftWingOuter = leftWing.addOrReplaceChild(
                    LEFT_WING_OUTER,
                    CubeListBuilder.create()
                        .texOffs(32, 12)
                        .addBox(0.0f, -1.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    PartPose.offsetAndRotation(11.0f, 1.0f, 0.0f, 0.0f, 1.0908f, 0.0f)
                )

                val rightWing = body.addOrReplaceChild(
                    RIGHT_WING,
                    CubeListBuilder.create()
                        .texOffs(32, 0)
                        .mirror()
                        .addBox(-11.0f, 0.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    PartPose.offsetAndRotation(-4.0f, 1.0f, 2.0f, 0.0f, 0.5236f, 0.0f)
                )

                val rightWingOuter = rightWing.addOrReplaceChild(
                    RIGHT_WING_OUTER,
                    CubeListBuilder.create()
                        .texOffs(32, 12)
                        .mirror()
                        .addBox(-11.0f, -1.0f, 0.0f, 11.0f, 12.0f, 0.0f),
                    PartPose.offsetAndRotation(-11.0f, 1.0f, 0.0f, 0.0f, -1.0908f, 0.0f)
                )

                val legs = body.addOrReplaceChild(
                    LEGS,
                    CubeListBuilder.create()
                        .texOffs(0, 31)
                        .addBox(-4.0f, 0.0f, 0.0f, 8.0f, 4.0f, 0.0f),
                    PartPose.offsetAndRotation(0.0f, 13.0f, 1.0f, -0.9599f, 0.0f, 0.0f)
                )

                val talons = legs.addOrReplaceChild(
                    TALONS,
                    CubeListBuilder.create()
                        .texOffs(0, 35)
                        .addBox(-4.0f, 0.0f, -1.0f, 3.0f, 4.0f, 4.0f)
                        .texOffs(0, 35)
                        .mirror()
                        .addBox(1.0f, 0.0f, -1.0f, 3.0f, 4.0f, 4.0f),
                    PartPose.offsetAndRotation(0.0f, 4.0f, 0.0f, -0.4363f, 0.0f, 0.0f)
                )

                val tailbone = body.addOrReplaceChild(
                    TAILBONE,
                    CubeListBuilder.create()
                        .texOffs(32, 36)
                        .addBox(-1.0f, -0.0912f, -2.1233f, 2.0f, 10.0f, 2.0f),
                    PartPose.offsetAndRotation(0.0f, 11.5912f, 3.1233f, 0.6109f, 0.0f, 0.0f)
                )

                val head = root.addOrReplaceChild(
                    HEAD,
                    CubeListBuilder.create()
                        .texOffs(16, 34)
                        .addBox(-2.0f, -3.0f, -9.0f, 4.0f, 3.0f, 4.0f)
                        .texOffs(0, 19)
                        .addBox(-4.0f, -6.0f, -5.0f, 8.0f, 6.0f, 6.0f)
                        .texOffs(16, 41)
                        .mirror()
                        .addBox(1.0f, -11.0f, -4.0f, 4.0f, 6.0f, 1.0f)
                        .texOffs(16, 41)
                        .mirror(false)
                        .addBox(-5.0f, -11.0f, -4.0f, 4.0f, 6.0f, 1.0f),
                    PartPose.offsetAndRotation(0.0f, 7.0f, 0.0f, 0.3491f, 0.0f, 0.0f)
                )

                val fangs = head.addOrReplaceChild(
                    FANGS,
                    CubeListBuilder.create()
                        .texOffs(12, 35)
                        .addBox(-2.0f, -1.0f, -9.0f, 4.0f, 2.0f, 0.0f),
                    PartPose.ZERO
                )

                return LayerDefinition.create(data, 64, 48)
            }
    }
}
