package dev.andante.direbats.tag

import dev.andante.direbats.Direbats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType

object DirebatsEntityTypeTags {
    /**
     * Represents a list of entity types not affected by the effects of a Direbat Fang Arrow.
     */
    val DIREBAT_FANG_ARROW_EFFECTS_IMMUNE = register("direbat_fang_arrow_effects_immune")

    private fun register(id: String): TagKey<EntityType<*>> {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id))
    }
}
