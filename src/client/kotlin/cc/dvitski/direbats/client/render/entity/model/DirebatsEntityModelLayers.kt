package cc.dvitski.direbats.client.render.entity.model

import cc.dvitski.direbats.Direbats
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.resources.Identifier

/**
 * Represents Direbats entity model layers.
 */
@Environment(EnvType.CLIENT)
object DirebatsEntityModelLayers {
    val DIREBAT = registerMain("direbat")

    private fun registerMain(id: String): ModelLayerLocation {
        val layer = ModelLayerLocation(Identifier.fromNamespaceAndPath(Direbats.MOD_ID, id), "main")
        ModelLayerRegistry.registerModelLayer(layer, DirebatEntityModel::TEXTURED_MODEL_DATA)
        return layer
    }
}
