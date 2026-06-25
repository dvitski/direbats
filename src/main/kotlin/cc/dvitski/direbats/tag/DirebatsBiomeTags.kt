package cc.dvitski.direbats.tag

import cc.dvitski.direbats.Direbats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome

object DirebatsBiomeTags {
    /**
     * Represents the list of biomes that can spawn Direbats.
     */
    val DIREBAT_CAN_SPAWN = register("direbat_can_spawn")

    private fun register(id: String): TagKey<Biome> {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Direbats.MOD_ID, id))
    }
}
