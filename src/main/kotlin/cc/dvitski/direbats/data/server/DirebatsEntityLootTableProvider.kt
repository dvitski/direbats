package cc.dvitski.direbats.data.server

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider
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
class DirebatsEntityLootTableProvider(out: FabricPackOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricEntityLootSubProvider(out, lookup) {
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
