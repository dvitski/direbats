package cc.dvitski.direbats.world

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.level.gamerules.GameRule
import net.minecraft.world.level.gamerules.GameRuleCategory
import net.minecraft.world.level.gamerules.GameRuleType
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor
import net.minecraft.world.level.gamerules.GameRules.VisitorCaller
import java.util.function.ToIntFunction

/**
 * Represents Direbats game rules.
 */
object DirebatsGameRules {
    /**
     * Whether or not Direbats can locate and loot item entities.
     */
    val DO_DIREBAT_ITEM_PICKUP: GameRule<Boolean> = registerBoolean("direbat_item_pickup", GameRuleCategory.MOBS, true)

    private fun registerBoolean(id: String, category: GameRuleCategory, defaultValue: Boolean): GameRule<Boolean> {
        return register(
            id,
            category,
            GameRuleType.BOOL,
            BoolArgumentType.bool(),
            Codec.BOOL,
            defaultValue,
            FeatureFlagSet.of(),
            GameRuleTypeVisitor::visitBoolean,
             { b -> if (b) 1 else 0 },
        )
    }

    private fun <T : Any> register(
        id: String,
        category: GameRuleCategory,
        typeHint: GameRuleType,
        argumentType: ArgumentType<T>,
        codec: Codec<T>,
        defaultValue: T,
        requiredFeatures: FeatureFlagSet,
        visitorCaller: VisitorCaller<T>,
        commandResultFunction: ToIntFunction<T>,
    ): GameRule<T> {
        val rule = GameRule(category, typeHint, argumentType, visitorCaller, codec, commandResultFunction, defaultValue, requiredFeatures)
        Registry.register(BuiltInRegistries.GAME_RULE, id, rule)
        return rule
    }
}
