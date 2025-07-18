package dev.andante.direbats.entity

import dev.andante.direbats.Direbats
import dev.andante.direbats.item.DirebatsItems
import dev.andante.direbats.tag.DirebatsBiomeTags
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.block.DispenserBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.SpawnLocationTypes
import net.minecraft.entity.SpawnRestriction
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.Heightmap

/**
 * Represents Direbats entities.
 */
object DirebatsEntityTypes {
    /**
     * Represents a Direbat entity.
     * @see [DirebatEntity]
     */
    val DIREBAT: EntityType<DirebatEntity> = register("direbat",
        EntityType.Builder.create(::DirebatEntity, SpawnGroup.MONSTER)
            .eyeHeight(0.5f)
            .dimensions(0.95F, 0.9F)
    )

    /**
     * Represents a Direbat Fang Arrow entity.
     * @see [DirebatFangArrowEntity]
     */
    val DIREBAT_FANG_ARROW: EntityType<DirebatFangArrowEntity> = register("direbat_fang_arrow",
        EntityType.Builder.create(::DirebatFangArrowEntity, SpawnGroup.MISC)
            .dimensions(0.5F, 0.5F)
            .maxTrackingRange(4)
            .trackingTickInterval(20)
    )

    init {
        FabricDefaultAttributeRegistry.register(DIREBAT, DirebatEntity.createDirebatAttributes())
        DispenserBlock.registerProjectileBehavior(DirebatsItems.DIREBAT_FANG_ARROW)
        SpawnRestriction.register(DIREBAT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DirebatEntity::canSpawn)
        BiomeModifications.addSpawn(BiomeSelectors.tag(DirebatsBiomeTags.DIREBAT_CAN_SPAWN), SpawnGroup.MONSTER, DIREBAT, 36, 1, 3)
    }

    private fun <T : Entity> register(id: String, builder: EntityType.Builder<T>): EntityType<T> {
        val identifier = Identifier.of(Direbats.MOD_ID, id)
        val key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, identifier)
        return Registry.register(Registries.ENTITY_TYPE, identifier, builder.build(key))
    }
}
