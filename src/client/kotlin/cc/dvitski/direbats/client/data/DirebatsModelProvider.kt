package cc.dvitski.direbats.client.data

import cc.dvitski.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

/**
 * Generates Direbats item models.
 */
class DirebatsModelProvider(out: FabricPackOutput) : FabricModelProvider(out) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
    }

    override fun generateItemModels(generator: ItemModelGenerators) {
        generator.generateFlatItem(DirebatsItems.DIREBAT_SPAWN_EGG, ModelTemplates.FLAT_ITEM)
        generator.generateFlatItem(DirebatsItems.DIREBAT_FANG, ModelTemplates.FLAT_ITEM)
        generator.generateFlatItem(DirebatsItems.DIREBAT_FANG_ARROW, ModelTemplates.FLAT_ITEM)
    }
}
