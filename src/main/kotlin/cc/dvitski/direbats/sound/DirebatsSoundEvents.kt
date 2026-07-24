package cc.dvitski.direbats.sound

import cc.dvitski.direbats.entity.DirebatsEntityTypes
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType

object DirebatsSoundEvents {
    val ENTITY_DIREBAT_AMBIENT = direbat("ambient")
    val ENTITY_DIREBAT_HURT = direbat("hurt")
    val ENTITY_DIREBAT_ATTACK = direbat("attack")
    val ENTITY_DIREBAT_DEATH = direbat("death")

    private fun direbat(id: String): SoundEvent {
        return registerEntity(DirebatsEntityTypes.DIREBAT, id)
    }

    private fun registerEntity(entity: EntityType<*>, id: String): SoundEvent {
        val identifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity)
        val soundResourceLocation = ResourceLocation.fromNamespaceAndPath(identifier.namespace, "entity.${identifier.path}.$id")
        return Registry.register(BuiltInRegistries.SOUND_EVENT, soundResourceLocation, SoundEvent.createVariableRangeEvent(soundResourceLocation))
    }
}
