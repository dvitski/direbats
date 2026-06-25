package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.item.DirebatsItemIds
import cc.dvitski.direbats.tag.DirebatsItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.HolderLookup
import net.minecraft.references.BlockItemIds
import net.minecraft.references.ItemIds
import net.minecraft.tags.ItemTags
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats item tags.
 */
class DirebatsItemTagProvider(out: FabricPackOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider.ItemTagsProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        builder(DirebatsItemTags.PICKED_UP_BY_DIREBAT)
            .forceAddTag(ItemTags.ARROWS)
            .forceAddTag(ConventionalItemTags.EMPTY_BUCKETS)
            .forceAddTag(ConventionalItemTags.WATER_BUCKETS)
            .forceAddTag(ConventionalItemTags.LAVA_BUCKETS)
            .forceAddTag(ConventionalItemTags.MILK_BUCKETS)
            .forceAddTag(ConventionalItemTags.FOODS)
            .forceAddTag(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
            .also { builder ->
                builder.add(
                    ItemIds.WOODEN_SWORD,
                    ItemIds.WOODEN_SHOVEL,
                    ItemIds.WOODEN_PICKAXE,
                    ItemIds.WOODEN_AXE,
                    ItemIds.WOODEN_HOE,
                    ItemIds.STONE_SWORD,
                    ItemIds.STONE_SHOVEL,
                    ItemIds.STONE_PICKAXE,
                    ItemIds.STONE_AXE,
                    ItemIds.STONE_HOE,
                    ItemIds.GOLDEN_SWORD,
                    ItemIds.GOLDEN_SHOVEL,
                    ItemIds.GOLDEN_PICKAXE,
                    ItemIds.GOLDEN_AXE,
                    ItemIds.GOLDEN_HOE,
                    ItemIds.IRON_SWORD,
                    ItemIds.IRON_SHOVEL,
                    ItemIds.IRON_PICKAXE,
                    ItemIds.IRON_AXE,
                    ItemIds.IRON_HOE,
                    ItemIds.DIAMOND_SWORD,
                    ItemIds.DIAMOND_SHOVEL,
                    ItemIds.DIAMOND_PICKAXE,
                    ItemIds.DIAMOND_AXE,
                    ItemIds.DIAMOND_HOE,
                    ItemIds.NETHERITE_SWORD,
                    ItemIds.NETHERITE_SHOVEL,
                    ItemIds.NETHERITE_PICKAXE,
                    ItemIds.NETHERITE_AXE,
                    ItemIds.NETHERITE_HOE,
                    ItemIds.SHEARS,
                    ItemIds.MACE,
                    ItemIds.TRIDENT,
                )
            }

        builder(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS).add(
            ItemIds.EGG,
            BlockItemIds.DRAGON_EGG.item,
            BlockItemIds.TURTLE_EGG.item,
        )

        builder(ItemTags.ARROWS).add(
            DirebatsItemIds.DIREBAT_FANG_ARROW
        )
    }
}
