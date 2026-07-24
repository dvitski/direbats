package cc.dvitski.direbats.item

import cc.dvitski.direbats.entity.DirebatFangArrowEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ArrowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

/**
 * Represents a Direbat Fang Arrow item.
 * @see [DirebatsItems.DIREBAT_FANG_ARROW]
 */
class DirebatFangArrowItem(settings: Properties) : ArrowItem(settings) {
    override fun createArrow(world: Level, stack: ItemStack, shooter: LivingEntity, shotFrom: ItemStack?): AbstractArrow {
        return DirebatFangArrowEntity(world, shooter, stack.copyWithCount(1), shotFrom)
    }
}
