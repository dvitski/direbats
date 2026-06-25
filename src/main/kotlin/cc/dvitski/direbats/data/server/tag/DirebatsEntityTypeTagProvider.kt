package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.entity.DirebatsEntityTypeIds
import cc.dvitski.direbats.tag.DirebatsEntityTypeTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.EntityTypeTags
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats entity type tags.
 */
class DirebatsEntityTypeTagProvider(out: FabricPackOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider.EntityTypeTagsProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        builder(DirebatsEntityTypeTags.DIREBAT_FANG_ARROW_EFFECTS_IMMUNE)
            .add(ResourceKey.create(Registries.ENTITY_TYPE, DirebatsEntityTypeIds.DIREBAT))
            .forceAddTag(ConventionalEntityTypeTags.BOSSES)

        builder(EntityTypeTags.ARROWS)
            .add(ResourceKey.create(Registries.ENTITY_TYPE, DirebatsEntityTypeIds.DIREBAT))

        builder(EntityTypeTags.BURN_IN_DAYLIGHT)
            .add(ResourceKey.create(Registries.ENTITY_TYPE, DirebatsEntityTypeIds.DIREBAT))
    }
}
