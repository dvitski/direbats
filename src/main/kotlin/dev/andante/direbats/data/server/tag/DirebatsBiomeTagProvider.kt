package dev.andante.direbats.data.server.tag

import dev.andante.direbats.tag.DirebatsBiomeTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.Biome
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats biome tags.
 */
class DirebatsBiomeTagProvider(out: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagProvider<Biome>(out, Registries.BIOME, registriesFuture)  {
    override fun addTags(arg: HolderLookup.Provider) {
        builder(DirebatsBiomeTags.DIREBAT_CAN_SPAWN)
            .forceAddTag(ConventionalBiomeTags.IS_FOREST)
            .forceAddTag(ConventionalBiomeTags.IS_TAIGA)
    }
}
