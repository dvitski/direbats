package dev.andante.direbats.entity

import dev.andante.direbats.item.DirebatsItems
import dev.andante.direbats.tag.DirebatsEntityTypeTags
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.world.World

/**
 * Represents a Direbat Fang Arrow entity.
 * @see [DirebatsEntityTypes.DIREBAT_FANG_ARROW]
 */
class DirebatFangArrowEntity : PersistentProjectileEntity {
    private var duration = 300

    constructor(entityType: EntityType<out DirebatFangArrowEntity>, world: World) : super(entityType, world)
    constructor(world: World, owner: LivingEntity, stack: ItemStack, shotFrom: ItemStack?) : super(DirebatsEntityTypes.DIREBAT_FANG_ARROW, owner, world, stack, shotFrom)

    override fun tick() {
        super.tick()

        if (world.isClient) {
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
            world.addParticleClient(ParticleTypes.INSTANT_EFFECT, x, y, z, 0.0, 0.0, 0.0)
        }
    }

    override fun onHit(target: LivingEntity) {
        super.onHit(target)

        if (!target.type.isIn(DirebatsEntityTypeTags.DIREBAT_FANG_ARROW_EFFECTS_IMMUNE)) {
            // blind
            target.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, duration, 0), this.effectCause)

            // simulate confusion caused by blindness if mob
            if (target is MobEntity && target.target != null) {
                // clear visibility cache
                target.visibilityCache.clear()

                // cancel all goals (simulate confusion)
                listOf(target.goalSelector, target.targetSelector).forEach { it.goals.forEach(Goal::stop) }

                // clear targetting variables
                target.target = null
                target.attacker = null
            }
        }
    }

    override fun getDefaultItemStack(): ItemStack {
        return ItemStack(DirebatsItems.DIREBAT_FANG_ARROW)
    }

    /* NBT */

    override fun writeCustomData(view: WriteView) {
        super.writeCustomData(view)
        view.putInt(DURATION_KEY, duration)
    }

    override fun readCustomData(view: ReadView) {
        super.readCustomData(view)
        duration = view.getInt(DURATION_KEY, 300)
    }

    companion object {
        const val DURATION_KEY = "Duration"
    }
}
