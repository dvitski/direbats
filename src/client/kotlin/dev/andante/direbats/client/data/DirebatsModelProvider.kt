package dev.andante.direbats.client.data

import dev.andante.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

/**
 * Generates Direbats item models.
 */
class DirebatsModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
    }

    override fun generateItemModels(generator: ItemModelGenerators) {
        generator.generateFlatItem(DirebatsItems.DIREBAT_SPAWN_EGG, ModelTemplates.FLAT_ITEM)
        generator.generateFlatItem(DirebatsItems.DIREBAT_FANG, ModelTemplates.FLAT_ITEM)
        generator.generateFlatItem(DirebatsItems.DIREBAT_FANG_ARROW, ModelTemplates.FLAT_ITEM)
    }
}
