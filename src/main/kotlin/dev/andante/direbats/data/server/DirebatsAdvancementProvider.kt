package dev.andante.direbats.data.server

import dev.andante.direbats.Direbats
import dev.andante.direbats.data.DirebatsAdvancementLanguageStrings
import dev.andante.direbats.entity.DirebatsEntityTypes
import dev.andante.direbats.tag.DirebatsItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementDisplay
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.condition.EntityPropertiesLootCondition
import net.minecraft.loot.context.LootContext.EntityTarget
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Generates Direbats advancements.
 */
class DirebatsAdvancementProvider(out: FabricDataOutput, lookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricAdvancementProvider(out, lookup) {
    override fun generateAdvancement(lookup: RegistryWrapper.WrapperLookup, exporter: Consumer<AdvancementEntry>) {
        Advancement.Builder.create()
            .parent(Advancement.Builder.create().build(Identifier.of("adventure/root")))
            .display(AdvancementDisplay(
                ItemStack(Items.EGG),
                Text.translatable(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_TITLE),
                Text.translatable(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_DESCRIPTION),
                Optional.empty(), AdvancementFrame.CHALLENGE, true, true, false
            ))
            .criterion(
                "pick_up_egg",
                ThrownItemPickedUpByEntityCriterion.Conditions.createThrownItemPickedUpByEntity(
                    LootContextPredicate.create(),
                    Optional.of(
                        ItemPredicate.Builder.create()
                            .tag(lookup.getOrThrow(RegistryKeys.ITEM), DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
                            .build()
                    ),
                    Optional.of(
                        LootContextPredicate.create(
                            EntityPropertiesLootCondition.builder(
                                EntityTarget.THIS,
                                EntityPredicate.Builder.create()
                                    .type(lookup.getOrThrow(RegistryKeys.ENTITY_TYPE), DirebatsEntityTypes.DIREBAT)
                            ).build()
                        )
                    )
                )
            )
            .build(Identifier.of(Direbats.MOD_ID, "direbat_picks_up_egg"))
            .let(exporter::accept)
    }
}
