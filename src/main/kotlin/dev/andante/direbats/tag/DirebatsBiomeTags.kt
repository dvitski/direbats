package dev.andante.direbats.tag

import dev.andante.direbats.Direbats
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

object DirebatsBiomeTags {
    /**
     * Represents the list of biomes that can spawn Direbats.
     */
    val DIREBAT_CAN_SPAWN = register("direbat_can_spawn")

    private fun register(id: String): TagKey<Biome> {
        return TagKey.of(RegistryKeys.BIOME, Identifier.of(Direbats.MOD_ID, id))
    }
}
