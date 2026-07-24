package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.tag.DirebatsEntityTypeTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.EntityTypeTags
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats entity type tags.
 */
class DirebatsEntityTypeTagProvider(out: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagProvider.EntityTypeTagProvider(out, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(DirebatsEntityTypeTags.DIREBAT_FANG_ARROW_EFFECTS_IMMUNE)
            .add(DirebatsEntityTypes.DIREBAT)
            .forceAddTag(ConventionalEntityTypeTags.BOSSES)

        getOrCreateTagBuilder(EntityTypeTags.ARROWS)
            .add(DirebatsEntityTypes.DIREBAT)
    }
}
