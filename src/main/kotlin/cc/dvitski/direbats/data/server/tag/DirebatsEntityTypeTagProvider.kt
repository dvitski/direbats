package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.tag.DirebatsEntityTypeTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.EntityTypeTags
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats entity type tags.
 */
class DirebatsEntityTypeTagProvider(out: FabricPackOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider.EntityTypeTagsProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        valueLookupBuilder(DirebatsEntityTypeTags.DIREBAT_FANG_ARROW_EFFECTS_IMMUNE)
            .add(DirebatsEntityTypes.DIREBAT)
            .forceAddTag(ConventionalEntityTypeTags.BOSSES)

        valueLookupBuilder(EntityTypeTags.ARROWS)
            .add(DirebatsEntityTypes.DIREBAT)

        valueLookupBuilder(EntityTypeTags.BURN_IN_DAYLIGHT)
            .add(DirebatsEntityTypes.DIREBAT)
    }
}
