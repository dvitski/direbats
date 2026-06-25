package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.tag.DirebatsItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats item tags.
 */
class DirebatsItemTagProvider(out: FabricPackOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider.ItemTagsProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        valueLookupBuilder(DirebatsItemTags.PICKED_UP_BY_DIREBAT)
            .forceAddTag(ItemTags.ARROWS)
            .forceAddTag(ConventionalItemTags.EMPTY_BUCKETS)
            .forceAddTag(ConventionalItemTags.WATER_BUCKETS)
            .forceAddTag(ConventionalItemTags.LAVA_BUCKETS)
            .forceAddTag(ConventionalItemTags.MILK_BUCKETS)
            .forceAddTag(ConventionalItemTags.FOODS)
            .forceAddTag(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
            .also { builder ->
                builder.add(
                    Items.WOODEN_SWORD,
                    Items.WOODEN_SHOVEL,
                    Items.WOODEN_PICKAXE,
                    Items.WOODEN_AXE,
                    Items.WOODEN_HOE,
                    Items.STONE_SWORD,
                    Items.STONE_SHOVEL,
                    Items.STONE_PICKAXE,
                    Items.STONE_AXE,
                    Items.STONE_HOE,
                    Items.GOLDEN_SWORD,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.IRON_SWORD,
                    Items.IRON_SHOVEL,
                    Items.IRON_PICKAXE,
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.DIAMOND_SWORD,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_HOE,
                    Items.NETHERITE_SWORD,
                    Items.NETHERITE_SHOVEL,
                    Items.NETHERITE_PICKAXE,
                    Items.NETHERITE_AXE,
                    Items.NETHERITE_HOE,
                    Items.SHEARS,
                    Items.MACE,
                    Items.TRIDENT,
                )
            }

        valueLookupBuilder(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS).add(
            Items.EGG,
            Items.DRAGON_EGG,
            Items.TURTLE_EGG
        )

        valueLookupBuilder(ItemTags.ARROWS).add(
            DirebatsItems.DIREBAT_FANG_ARROW
        )
    }
}
