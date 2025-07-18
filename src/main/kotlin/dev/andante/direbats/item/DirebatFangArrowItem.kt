package dev.andante.direbats.item

import dev.andante.direbats.entity.DirebatFangArrowEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/**
 * Represents a Direbat Fang Arrow item.
 * @see [DirebatsItems.DIREBAT_FANG_ARROW]
 */
class DirebatFangArrowItem(settings: Settings) : ArrowItem(settings) {
    override fun createArrow(world: World, stack: ItemStack, shooter: LivingEntity, shotFrom: ItemStack?): PersistentProjectileEntity {
        return DirebatFangArrowEntity(world, shooter, stack.copyWithCount(1), shotFrom)
    }
}
