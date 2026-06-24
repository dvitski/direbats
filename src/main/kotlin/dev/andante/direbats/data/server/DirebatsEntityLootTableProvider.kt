package dev.andante.direbats.data.server

import dev.andante.direbats.entity.DirebatsEntityTypes
import dev.andante.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats entity loot tables.
 */
class DirebatsEntityLootTableProvider(out: FabricDataOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricEntityLootTableProvider(out, lookup) {
    override fun generate() {
        add(DirebatsEntityTypes.DIREBAT,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .add(
                            LootItem.lootTableItem(DirebatsItems.DIREBAT_FANG)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 2.0f)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0f, 4.0f)))
                        )
                        .`when`(
                            LootItemKilledByPlayerCondition.killedByPlayer()
                        )
                )
        )
    }
}
