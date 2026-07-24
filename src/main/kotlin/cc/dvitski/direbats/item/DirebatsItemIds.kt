package cc.dvitski.direbats.item

import cc.dvitski.direbats.Direbats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

object DirebatsItemIds {
    val DIREBAT_SPAWN_EGG = create("direbat_spawn_egg")
    val DIREBAT_FANG = create("direbat_fang")
    val DIREBAT_FANG_ARROW = create("direbat_fang_arrow")

    fun create(id: String): ResourceKey<Item> = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id))
}
