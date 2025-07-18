package dev.andante.direbats.item

import dev.andante.direbats.Direbats
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object DirebatsItemGroups {
    val ALL = register("all", FabricItemGroup.builder()
        .displayName(Text.translatable("itemGroup.${Direbats.MOD_ID}.all"))
        .entries { _, entries ->
            entries.add(DirebatsItems.DIREBAT_SPAWN_EGG)
            entries.add(DirebatsItems.DIREBAT_FANG)
            entries.add(DirebatsItems.DIREBAT_FANG_ARROW)
        }
        .icon { ItemStack(DirebatsItems.DIREBAT_FANG) })

    private fun register(id: String, builder: ItemGroup.Builder): ItemGroup {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(Direbats.MOD_ID, id), builder.build())
    }
}
