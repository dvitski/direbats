package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.tag.DirebatsBiomeTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.Biome
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats biome tags.
 */
class DirebatsBiomeTagProvider(out: FabricPackOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider<Biome>(out, Registries.BIOME, registriesFuture)  {
    override fun addTags(arg: HolderLookup.Provider) {
        builder(DirebatsBiomeTags.DIREBAT_CAN_SPAWN)
            .forceAddTag(ConventionalBiomeTags.IS_FOREST)
            .forceAddTag(ConventionalBiomeTags.IS_TAIGA)
    }
}
