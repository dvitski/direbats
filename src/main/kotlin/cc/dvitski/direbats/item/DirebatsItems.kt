package cc.dvitski.direbats.item

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.SpawnEggItem

/**
 * Represents Direbats items.
 */
object DirebatsItems {
    /**
     * Represents a Direbat Spawn Egg item.
     */
    val DIREBAT_SPAWN_EGG = register(DirebatsItemIds.DIREBAT_SPAWN_EGG, { props -> SpawnEggItem(DirebatsEntityTypes.DIREBAT, 0x4C4C4C, 0x120E14, props) })

    /**
     * Represents a Direbat Fang item.
     */
    val DIREBAT_FANG = register(DirebatsItemIds.DIREBAT_FANG)

    /**
     * Represents a Direbat Fang Arrow item.
     */
    val DIREBAT_FANG_ARROW = register(DirebatsItemIds.DIREBAT_FANG_ARROW, ::DirebatFangArrowItem)

    private fun register(key: ResourceKey<Item>, factory: (Item.Properties) -> Item = ::Item, settings: Item.Properties = Item.Properties()): Item {
        return Registry.register(BuiltInRegistries.ITEM, key, factory(settings))
    }
}
