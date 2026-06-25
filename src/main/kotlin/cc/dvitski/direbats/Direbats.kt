package cc.dvitski.direbats

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import cc.dvitski.direbats.item.DirebatsItemGroups
import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.sound.DirebatsSoundEvents
import cc.dvitski.direbats.tag.DirebatsGameEventTags
import cc.dvitski.direbats.tag.DirebatsItemTags
import cc.dvitski.direbats.world.DirebatsGameRules
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
