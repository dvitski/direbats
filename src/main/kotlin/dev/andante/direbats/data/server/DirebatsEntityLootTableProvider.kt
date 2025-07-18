package dev.andante.direbats.data.server

import dev.andante.direbats.entity.DirebatsEntityTypes
import dev.andante.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.KilledByPlayerLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats entity loot tables.
 */
class DirebatsEntityLootTableProvider(out: FabricDataOutput, lookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricEntityLootTableProvider(out, lookup) {
    override fun generate() {
        register(DirebatsEntityTypes.DIREBAT,
            LootTable.builder()
                .pool(
                    LootPool.builder()
                        .with(
                            ItemEntry.builder(DirebatsItems.DIREBAT_FANG)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0f, 4.0f)))
                        )
                        .conditionally(
                            KilledByPlayerLootCondition.builder()
                        )
                )
        )
    }
}
