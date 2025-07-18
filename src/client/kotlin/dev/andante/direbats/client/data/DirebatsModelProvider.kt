package dev.andante.direbats.client.data

import dev.andante.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models

/**
 * Generates Direbats item models.
 */
class DirebatsModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        generator.register(DirebatsItems.DIREBAT_SPAWN_EGG, Models.GENERATED)
        generator.register(DirebatsItems.DIREBAT_FANG, Models.GENERATED)
        generator.register(DirebatsItems.DIREBAT_FANG_ARROW, Models.GENERATED)
    }
}
