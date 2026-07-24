package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.tag.DirebatsItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats item tags.
 */
class DirebatsItemTagProvider(out: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagProvider.ItemTagProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(DirebatsItemTags.PICKED_UP_BY_DIREBAT)
            .forceAddTag(ItemTags.ARROWS)
            .forceAddTag(ConventionalItemTags.EMPTY_BUCKETS)
            .forceAddTag(ConventionalItemTags.WATER_BUCKETS)
            .forceAddTag(ConventionalItemTags.LAVA_BUCKETS)
            .forceAddTag(ConventionalItemTags.MILK_BUCKETS)
            .forceAddTag(ConventionalItemTags.FOODS)
            .forceAddTag(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
            .also { builder ->
                lookup.lookupOrThrow(Registries.ITEM).listElements().forEach { entry ->
                    if (entry.value().components().has(DataComponents.TOOL)) {
                        builder.add(entry.key())
                    }
                }
            }

        getOrCreateTagBuilder(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
            .add(Items.EGG)
            .add(Items.DRAGON_EGG)
            .add(Items.TURTLE_EGG)

        getOrCreateTagBuilder(ItemTags.ARROWS)
            .add(DirebatsItems.DIREBAT_FANG_ARROW)
    }
}
