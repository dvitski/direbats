package dev.andante.direbats.tag

import dev.andante.direbats.Direbats
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.event.GameEvent

object DirebatsGameEventTags {
    /**
     * Represents a list of game events that Direbats can listen to.
     */
    val DIREBAT_CAN_LISTEN = register("direbat_can_listen")

    private fun register(id: String): TagKey<GameEvent> {
        return TagKey.of(RegistryKeys.GAME_EVENT, Identifier.of(Direbats.MOD_ID, id))
    }
}
