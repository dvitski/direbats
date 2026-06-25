package cc.dvitski.direbats.entity

import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.tag.DirebatsEntityTypeTags
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SpellParticleOption
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

/**
 * Represents a Direbat Fang Arrow entity.
 * @see [DirebatsEntityTypes.DIREBAT_FANG_ARROW]
 */
class DirebatFangArrowEntity : AbstractArrow {
    private var duration = 300

    constructor(entityType: EntityType<out DirebatFangArrowEntity>, world: Level) : super(entityType, world)
    constructor(world: Level, owner: LivingEntity, stack: ItemStack, shotFrom: ItemStack?) : super(DirebatsEntityTypes.DIREBAT_FANG_ARROW, owner, world, stack, shotFrom)

    override fun tick() {
        super.tick()

        if (level().isClientSide) {
            if (isInGround) {
                if (inGroundTime % 5 == 0) {
                    spawnParticles(1)
                }
            } else {
                spawnParticles(2)
            }
        }
    }

    private fun spawnParticles(amount: Int) {
        (0 until amount).forEach { _ ->
            level().addParticle(SpellParticleOption.create(ParticleTypes.INSTANT_EFFECT, 0xEFE3DC, 1.0f), x, y, z, 0.0, 0.0, 0.0)
        }
    }

    override fun doPostHurtEffects(target: LivingEntity) {
        super.doPostHurtEffects(target)

        if (!target.`is`(DirebatsEntityTypeTags.DIREBAT_FANG_ARROW_EFFECTS_IMMUNE)) {
            // blind
            target.addEffect(MobEffectInstance(MobEffects.BLINDNESS, duration, 0), this.effectSource)

            // simulate confusion caused by blindness if mob
            if (target is Mob && target.target != null) {
                // clear visibility cache
                target.sensing.tick()

                // cancel all goals (simulate confusion)
                listOf(target.goalSelector, target.targetSelector).forEach { it.availableGoals.forEach(Goal::stop) }

                // clear targetting variables
                target.target = null
                target.setLastHurtByMob(null)
            }
        }
    }

    override fun getDefaultPickupItem(): ItemStack {
        return ItemStack(DirebatsItems.DIREBAT_FANG_ARROW)
    }

    /* NBT */

    override fun addAdditionalSaveData(view: ValueOutput) {
        super.addAdditionalSaveData(view)
        view.putInt(DURATION_KEY, duration)
    }

    override fun readAdditionalSaveData(view: ValueInput) {
        super.readAdditionalSaveData(view)
        duration = view.getIntOr(DURATION_KEY, 300)
    }

    companion object {
        const val DURATION_KEY = "Duration"
    }
}
