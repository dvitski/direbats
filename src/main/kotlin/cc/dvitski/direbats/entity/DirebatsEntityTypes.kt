package cc.dvitski.direbats.entity

import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.tag.DirebatsBiomeTags
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacementTypes
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.levelgen.Heightmap

/**
 * Represents Direbats entities.
 */
object DirebatsEntityTypes {
    /**
     * Represents a Direbat entity.
     * @see [DirebatEntity]
     */
    val DIREBAT: EntityType<DirebatEntity> = register(DirebatsEntityTypeIds.DIREBAT,
        EntityType.Builder.of(::DirebatEntity, MobCategory.MONSTER)
            .eyeHeight(0.5f)
            .sized(0.95F, 0.9F)
    )

    /**
     * Represents a Direbat Fang Arrow entity.
     * @see [DirebatFangArrowEntity]
     */
    val DIREBAT_FANG_ARROW: EntityType<DirebatFangArrowEntity> = register(DirebatsEntityTypeIds.DIREBAT_FANG_ARROW,
        EntityType.Builder.of(::DirebatFangArrowEntity, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
    )

    init {
        FabricDefaultAttributeRegistry.register(DIREBAT, DirebatEntity.createDirebatAttributes())
        DispenserBlock.registerProjectileBehavior(DirebatsItems.DIREBAT_FANG_ARROW)
        SpawnPlacements.register(DIREBAT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DirebatEntity::canSpawn)
        BiomeModifications.addSpawn(BiomeSelectors.tag(DirebatsBiomeTags.DIREBAT_CAN_SPAWN), MobCategory.MONSTER, DIREBAT, 36, 1, 3)
    }

    private fun <T : Entity> register(id: ResourceLocation, builder: EntityType.Builder<T>): EntityType<T> {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id.path))
    }
}
