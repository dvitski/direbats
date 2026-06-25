package cc.dvitski.direbats.item

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

/**
 * Represents Direbats items.
 */
object DirebatsItems {
    /**
     * Represents a Direbat Spawn Egg item.
     */
    val DIREBAT_SPAWN_EGG = Items.registerSpawnEgg(DirebatsItemIds.DIREBAT_SPAWN_EGG, DirebatsEntityTypes.DIREBAT)

    /**
     * Represents a Direbat Fang item.
     */
    val DIREBAT_FANG = register(DirebatsItemIds.DIREBAT_FANG)

    /**
     * Represents a Direbat Fang Arrow item.
     */
    val DIREBAT_FANG_ARROW = register(DirebatsItemIds.DIREBAT_FANG_ARROW, ::DirebatFangArrowItem)

    private fun register(key: ResourceKey<Item>, factory: (Item.Properties) -> Item = ::Item, settings: Item.Properties = Item.Properties()): Item {
        return Items.registerItem(key, factory, settings)
    }
}
