package dev.andante.direbats.item

import dev.andante.direbats.Direbats
import dev.andante.direbats.entity.DirebatsEntityTypes
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

/**
 * Represents Direbats items.
 */
object DirebatsItems {
    /**
     * Represents a Direbat Spawn Egg item.
     */
    val DIREBAT_SPAWN_EGG = register("direbat_spawn_egg", { SpawnEggItem(DirebatsEntityTypes.DIREBAT, it) })

    /**
     * Represents a Direbat Fang item.
     */
    val DIREBAT_FANG = register("direbat_fang")

    /**
     * Represents a Direbat Fang Arrow item.
     */
    val DIREBAT_FANG_ARROW = register("direbat_fang_arrow", ::DirebatFangArrowItem)

    private fun register(id: String, factory: (Item.Settings) -> Item = ::Item, settings: Item.Settings = Item.Settings()): Item {
        val identifier = Identifier.of(Direbats.MOD_ID, id)
        val key = RegistryKey.of(RegistryKeys.ITEM, identifier)
        return Items.register(key, factory, settings)
    }
}
