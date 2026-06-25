package cc.dvitski.direbats.data.server

import cc.dvitski.direbats.Direbats
import cc.dvitski.direbats.data.DirebatsAdvancementLanguageStrings
import cc.dvitski.direbats.data.DirebatsSubtitleNames
import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.world.DirebatsGameRules
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.gamerules.GameRule
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats language files.
 */
class DirebatsLanguageProvider(out: FabricPackOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(out, lookup) {
    override fun generateTranslations(lookup: HolderLookup.Provider, builder: TranslationBuilder) {
        builder.add("itemGroup.${Direbats.MOD_ID}.all", Direbats.MOD_NAME)

        builder.add(DirebatsEntityTypes.DIREBAT, "Direbat")
        builder.add(DirebatsEntityTypes.DIREBAT_FANG_ARROW, "Direbat Fang Arrow")

        builder.add(DirebatsItems.DIREBAT_SPAWN_EGG, "Direbat Spawn Egg")
        builder.add(DirebatsItems.DIREBAT_FANG, "Direbat Fang")
        builder.add(DirebatsItems.DIREBAT_FANG_ARROW, "Direbat Fang Arrow")

        DirebatsEntityTypes.DIREBAT.run {
            builder.subtitle(this, DirebatsSubtitleNames.AMBIENT, "Direbat squeaks menacingly")
            builder.subtitle(this, DirebatsSubtitleNames.HURT, "Direbat hurts")
            builder.subtitle(this, DirebatsSubtitleNames.ATTACK, "Direbat sinks its teeth")
            builder.subtitle(this, DirebatsSubtitleNames.DEATH, "Direbat dies")
        }

        builder.add(DirebatsGameRules.DO_DIREBAT_ITEM_PICKUP, "Direbats pick up dropped items")

        builder.add(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_TITLE, "Angry Bats")
        builder.add(DirebatsAdvancementLanguageStrings.DIREBAT_PICKS_UP_EGG_DESCRIPTION, "Drop an Egg on the ground and watch as a Direbat steals it")
    }

    companion object {
        fun TranslationBuilder.add(rule: GameRule<*>, value: String) {
            return add(rule.descriptionId, value)
        }

        fun TranslationBuilder.subtitle(entity: EntityType<*>, id: String, value: String) {
            return add(entity.getSubtitle(id), value)
        }

        fun EntityType<*>.getSubtitle(id: String): String {
            val identifier = BuiltInRegistries.ENTITY_TYPE.getKey(this)
            return "subtitles.${identifier.namespace}.entity.${identifier.path}.$id"
        }
    }
}
