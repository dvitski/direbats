package cc.dvitski.direbats.data.server

import cc.dvitski.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats recipes.
 */
class DirebatsRecipeProvider(out: FabricDataOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricRecipeProvider(out, lookup) {
    override fun buildRecipes(exporter: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DirebatsItems.DIREBAT_FANG_ARROW, 4)
            .define('#', Items.ARROW)
            .define('X', DirebatsItems.DIREBAT_FANG)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .unlockedBy("has_direbat_fang", has(DirebatsItems.DIREBAT_FANG))
            .save(exporter)
    }

    override fun getName(): String {
        return "Direbats Recipes"
    }
}
