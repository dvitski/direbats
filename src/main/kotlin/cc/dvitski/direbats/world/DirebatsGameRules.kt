package cc.dvitski.direbats.world

import cc.dvitski.direbats.Direbats
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameRules.BooleanValue
import net.minecraft.world.level.GameRules.Category

/**
 * Represents Direbats game rules.
 */
object DirebatsGameRules {
    /**
     * Whether or not Direbats can locate and loot item entities.
     */
    val DO_DIREBAT_ITEM_PICKUP: GameRules.Key<BooleanValue> =
        GameRuleRegistry.register("${Direbats.MOD_ID}:doDirebatItemPickup",
            Category.MOBS, GameRuleFactory.createBooleanRule(true)
        )
}
