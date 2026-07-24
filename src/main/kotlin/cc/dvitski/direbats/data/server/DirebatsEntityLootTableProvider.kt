package cc.dvitski.direbats.data.server

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.item.DirebatsItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

/**
 * Generates Direbats entity loot tables.
 */
class DirebatsEntityLootTableProvider(out: FabricDataOutput, private val lookupFuture: CompletableFuture<HolderLookup.Provider>) :
    SimpleFabricLootTableProvider(out, lookupFuture, LootContextParamSets.ENTITY) {
    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
        val registries = lookupFuture.join()
        output.accept(
            DirebatsEntityTypes.DIREBAT.defaultLootTable,
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
