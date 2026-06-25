package cc.dvitski.direbats.item

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.entity.DirebatsEntityTypes
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
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
    val DIREBAT_SPAWN_EGG = Items.registerSpawnEgg(DirebatsEntityTypes.DIREBAT)

    /**
     * Represents a Direbat Fang item.
     */
    val DIREBAT_FANG = register("direbat_fang")

    /**
     * Represents a Direbat Fang Arrow item.
     */
    val DIREBAT_FANG_ARROW = register("direbat_fang_arrow", ::DirebatFangArrowItem)

    private fun register(id: String, factory: (Item.Properties) -> Item = ::Item, settings: Item.Properties = Item.Properties()): Item {
        val identifier = Identifier.fromNamespaceAndPath(Direbats.MOD_ID, id)
        val key = ResourceKey.create(Registries.ITEM, identifier)
        return Items.registerItem(key, factory, settings)
    }
}
