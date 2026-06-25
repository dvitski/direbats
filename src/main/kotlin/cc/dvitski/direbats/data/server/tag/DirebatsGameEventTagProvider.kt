package cc.dvitski.direbats.data.server.tag

import cc.dvitski.direbats.tag.DirebatsGameEventTags
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.GameEventTags
import net.minecraft.world.level.gameevent.GameEvent
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats game event tags.
 */
class DirebatsGameEventTagProvider(out: FabricPackOutput, lookup: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider<GameEvent>(out, Registries.GAME_EVENT, lookup)  {
    override fun addTags(arg: HolderLookup.Provider) {
        builder(DirebatsGameEventTags.DIREBAT_CAN_LISTEN)
            .forceAddTag(GameEventTags.VIBRATIONS)
    }
}
