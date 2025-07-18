package dev.andante.direbats.data.server

import dev.andante.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats recipes.
 */
class DirebatsRecipeProvider(out: FabricDataOutput, lookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricRecipeProvider(out, lookup) {
    override fun getRecipeGenerator(lookup: RegistryWrapper.WrapperLookup, exporter: RecipeExporter): RecipeGenerator {
        return object : RecipeGenerator(lookup, exporter) {
            override fun generate() {
                ShapedRecipeJsonBuilder.create(lookup.getOrThrow(RegistryKeys.ITEM), RecipeCategory.COMBAT, DirebatsItems.DIREBAT_FANG_ARROW, 4)
                .input('#', Items.ARROW)
                .input('X', DirebatsItems.DIREBAT_FANG)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .criterion("has_direbat_fang", conditionsFromItem(DirebatsItems.DIREBAT_FANG))
                .offerTo(exporter)
            }
        }
    }

    override fun getName(): String {
        return "Direbats Recipes"
    }
}
