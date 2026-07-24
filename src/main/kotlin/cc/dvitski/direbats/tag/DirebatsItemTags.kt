package cc.dvitski.direbats.tag

import cc.dvitski.direbats.Direbats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object DirebatsItemTags {
    /**
     * Represents a list of items that can be picked up by Direbats.
     */
    val PICKED_UP_BY_DIREBAT = register("picked_up_by_direbat")

    /**
     * Represents a list of items that grant the Direbat picks up egg advancement.
     */
    val DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS = register("direbat_picks_up_egg_advancement_items")

    private fun register(id: String): TagKey<Item> {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id))
    }
}
