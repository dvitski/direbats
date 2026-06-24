package dev.andante.direbats.item

import dev.andante.direbats.Direbats
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object DirebatsItemGroups {
    val ALL = register("all", FabricItemGroup.builder()
        .title(Component.translatable("itemGroup.${Direbats.MOD_ID}.all"))
        .displayItems { _, entries ->
            entries.accept(DirebatsItems.DIREBAT_SPAWN_EGG)
            entries.accept(DirebatsItems.DIREBAT_FANG)
            entries.accept(DirebatsItems.DIREBAT_FANG_ARROW)
        }
        .icon { ItemStack(DirebatsItems.DIREBAT_FANG) })

    private fun register(id: String, builder: CreativeModeTab.Builder): CreativeModeTab {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id), builder.build())
    }
}
