package dev.andante.direbats.item

import dev.andante.direbats.Direbats
import dev.andante.direbats.entity.DirebatsEntityTypes
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.SpawnEggItem

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

    private fun register(id: String, factory: (Item.Properties) -> Item = ::Item, settings: Item.Properties = Item.Properties()): Item {
        val identifier = ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id)
        val key = ResourceKey.create(Registries.ITEM, identifier)
        return Items.registerItem(key, factory, settings)
    }
}
