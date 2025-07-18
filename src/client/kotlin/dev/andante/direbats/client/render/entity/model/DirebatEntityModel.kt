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
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames.BODY
import net.minecraft.client.render.entity.model.EntityModelPartNames.HEAD
import net.minecraft.client.render.entity.model.EntityModelPartNames.LEFT_WING
import net.minecraft.client.render.entity.model.EntityModelPartNames.RIGHT_WING
import net.minecraft.util.math.MathHelper

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

    override fun setAngles(state: DirebatEntityRenderState) {
        if (state.hanging) {
            head.pitch = state.pitch * (Math.PI.toFloat() / 180f)
            body.pitch = 0.0f
            rightWing.pitch = 0.0f
            leftWing.pitch = 0.0f
            leftWing.yaw = 1.5f
            rightWing.yaw = -leftWing.yaw
            leftWingOuter.yaw = 90.0f
            rightWingOuter.yaw = -leftWingOuter.yaw
        } else {
            head.pitch = state.pitch * (Math.PI / 180).toFloat()
            head.yaw = state.relativeHeadYaw * (Math.PI / 180).toFloat()
            val animationProgress = state.age
            body.pitch = (Math.PI / 4).toFloat() + MathHelper.cos(animationProgress * 0.1f) * 0.15f
            rightWing.yaw = MathHelper.cos(animationProgress * 0.4f) * Math.PI.toFloat() * 0.4f
            leftWing.yaw = -rightWing.yaw
            rightWingOuter.yaw = rightWing.yaw * 0.5f
            leftWingOuter.yaw = leftWing.yaw * 0.5f
        }
    }

    companion object {
        @Suppress("UNUSED_VARIABLE")
        val TEXTURED_MODEL_DATA: TexturedModelData
            get() {
                val data = ModelData()
                val root = data.root

                val body = root.addChild(
                    BODY,
                    ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 13.0f, 6.0f),
                    ModelTransform.of(0.0f, 6.5774f, 0.9063f, 0.8727f, 0.0f, 0.0f)
                )

                val leftWing = body.addChild(
                    LEFT_WING,
                    ModelPartBuilder.create()
                        .uv(32, 0)
                        .cuboid(0.0f, 0.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    ModelTransform.of(4.0f, 1.0f, 2.0f, 0.0f, -0.5236f, 0.0f)
                )

                val leftWingOuter = leftWing.addChild(
                    LEFT_WING_OUTER,
                    ModelPartBuilder.create()
                        .uv(32, 12)
                        .cuboid(0.0f, -1.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    ModelTransform.of(11.0f, 1.0f, 0.0f, 0.0f, 1.0908f, 0.0f)
                )

                val rightWing = body.addChild(
                    RIGHT_WING,
                    ModelPartBuilder.create()
                        .uv(32, 0)
                        .mirrored()
                        .cuboid(-11.0f, 0.0f, 0.0f, 11.0f, 13.0f, 0.0f),
                    ModelTransform.of(-4.0f, 1.0f, 2.0f, 0.0f, 0.5236f, 0.0f)
                )

                val rightWingOuter = rightWing.addChild(
                    RIGHT_WING_OUTER,
                    ModelPartBuilder.create()
                        .uv(32, 12)
                        .mirrored()
                        .cuboid(-11.0f, -1.0f, 0.0f, 11.0f, 12.0f, 0.0f),
                    ModelTransform.of(-11.0f, 1.0f, 0.0f, 0.0f, -1.0908f, 0.0f)
                )

                val legs = body.addChild(
                    LEGS,
                    ModelPartBuilder.create()
                        .uv(0, 31)
                        .cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 4.0f, 0.0f),
                    ModelTransform.of(0.0f, 13.0f, 1.0f, -0.9599f, 0.0f, 0.0f)
                )

                val talons = legs.addChild(
                    TALONS,
                    ModelPartBuilder.create()
                        .uv(0, 35)
                        .cuboid(-4.0f, 0.0f, -1.0f, 3.0f, 4.0f, 4.0f)
                        .uv(0, 35)
                        .mirrored()
                        .cuboid(1.0f, 0.0f, -1.0f, 3.0f, 4.0f, 4.0f),
                    ModelTransform.of(0.0f, 4.0f, 0.0f, -0.4363f, 0.0f, 0.0f)
                )

                val tailbone = body.addChild(
                    TAILBONE,
                    ModelPartBuilder.create()
                        .uv(32, 36)
                        .cuboid(-1.0f, -0.0912f, -2.1233f, 2.0f, 10.0f, 2.0f),
                    ModelTransform.of(0.0f, 11.5912f, 3.1233f, 0.6109f, 0.0f, 0.0f)
                )

                val head = root.addChild(
                    HEAD,
                    ModelPartBuilder.create()
                        .uv(16, 34)
                        .cuboid(-2.0f, -3.0f, -9.0f, 4.0f, 3.0f, 4.0f)
                        .uv(0, 19)
                        .cuboid(-4.0f, -6.0f, -5.0f, 8.0f, 6.0f, 6.0f)
                        .uv(16, 41)
                        .mirrored()
                        .cuboid(1.0f, -11.0f, -4.0f, 4.0f, 6.0f, 1.0f)
                        .uv(16, 41)
                        .mirrored(false)
                        .cuboid(-5.0f, -11.0f, -4.0f, 4.0f, 6.0f, 1.0f),
                    ModelTransform.of(0.0f, 7.0f, 0.0f, 0.3491f, 0.0f, 0.0f)
                )

                val fangs = head.addChild(
                    FANGS,
                    ModelPartBuilder.create()
                        .uv(12, 35)
                        .cuboid(-2.0f, -1.0f, -9.0f, 4.0f, 2.0f, 0.0f),
                    ModelTransform.NONE
                )

                return TexturedModelData.of(data, 64, 48)
            }
    }
}
