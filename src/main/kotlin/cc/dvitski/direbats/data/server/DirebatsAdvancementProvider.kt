package cc.dvitski.direbats.data.server

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.data.DirebatsAdvancementLanguageStrings
import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.tag.DirebatsItemTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.DisplayInfo
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.PickedUpItemTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Generates Direbats advancements.
 */
class DirebatsAdvancementProvider(out: FabricDataOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricAdvancementProvider(out, lookup) {
    override fun generateAdvancement(lookup: HolderLookup.Provider, exporter: Consumer<AdvancementHolder>) {
        Advancement.Builder.advancement()
            .parent(Advancement.Builder.advancement().build(ResourceLocation.parse("adventure/root")))
            .display(
                DisplayInfo(
                ItemStack(Items.EGG),
                Component.translatable(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_TITLE),
                Component.translatable(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_DESCRIPTION),
                Optional.empty(), AdvancementType.CHALLENGE, true, true, false
            ))
            .addCriterion(
                "pick_up_egg",
                PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByEntity(
                    ContextAwarePredicate.create(),
                    Optional.of(
                        ItemPredicate.Builder.item()
                            .of(DirebatsItemTags.DIREBAT_PICKS_UP_EGG_ADVANCEMENT_ITEMS)
                            .build()
                    ),
                    Optional.of(
                        ContextAwarePredicate.create(
                            LootItemEntityPropertyCondition.hasProperties(
                                EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                    .of(DirebatsEntityTypes.DIREBAT)
                            ).build()
                        )
                    )
                )
            )
            .build(ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, "direbat_picks_up_egg"))
            .let(exporter::accept)
    }
}
