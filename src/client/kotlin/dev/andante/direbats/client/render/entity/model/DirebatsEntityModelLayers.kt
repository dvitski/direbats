package dev.andante.direbats.client.render.entity.model

import dev.andante.direbats.Direbats
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

/**
 * Represents Direbats entity model layers.
 */
@Environment(EnvType.CLIENT)
object DirebatsEntityModelLayers {
    val DIREBAT = registerMain("direbat")

    private fun registerMain(id: String): EntityModelLayer {
        val layer = EntityModelLayer(Identifier.of(Direbats.MOD_ID, id), "main")
        EntityModelLayerRegistry.registerModelLayer(layer, DirebatEntityModel::TEXTURED_MODEL_DATA)
        return layer
    }
}
