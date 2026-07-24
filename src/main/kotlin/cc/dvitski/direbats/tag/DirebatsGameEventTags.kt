package cc.dvitski.direbats.tag

import cc.dvitski.direbats.Direbats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.gameevent.GameEvent

object DirebatsGameEventTags {
    /**
     * Represents a list of game events that Direbats can listen to.
     */
    val DIREBAT_CAN_LISTEN = register("direbat_can_listen")

    private fun register(id: String): TagKey<GameEvent> {
        return TagKey.create(Registries.GAME_EVENT, ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id))
    }
}
