package dev.andante.direbats

import dev.andante.direbats.entity.DirebatsEntityTypes
import dev.andante.direbats.item.DirebatsItemGroups
import dev.andante.direbats.item.DirebatsItems
import dev.andante.direbats.sound.DirebatsSoundEvents
import dev.andante.direbats.tag.DirebatsGameEventTags
import dev.andante.direbats.tag.DirebatsItemTags
import dev.andante.direbats.world.DirebatsGameRules
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Direbats : ModInitializer {
    const val MOD_ID = "direbats"
    const val MOD_NAME = "Direbats"
    val LOGGER: Logger = LogManager.getLogger(MOD_NAME)

    override fun onInitialize() {
        LOGGER.info("Initializing $MOD_NAME")

        DirebatsGameRules
        DirebatsItemTags
        DirebatsEntityTypes
        DirebatsGameEventTags
        DirebatsItemGroups
        DirebatsItems
        DirebatsSoundEvents
    }
}
