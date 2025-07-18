package dev.andante.direbats.data.server.tag

import dev.andante.direbats.tag.DirebatsGameEventTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.GameEventTags
import net.minecraft.world.event.GameEvent
import java.util.concurrent.CompletableFuture

/**
 * Generates Direbats game event tags.
 */
class DirebatsGameEventTagProvider(out: FabricDataOutput, lookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricTagProvider<GameEvent>(out, RegistryKeys.GAME_EVENT, lookup)  {
    override fun configure(arg: RegistryWrapper.WrapperLookup) {
        builder(DirebatsGameEventTags.DIREBAT_CAN_LISTEN)
            .forceAddTag(GameEventTags.VIBRATIONS)
    }
}
