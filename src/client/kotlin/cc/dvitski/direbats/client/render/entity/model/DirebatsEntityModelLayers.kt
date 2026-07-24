package cc.dvitski.direbats.client.render.entity.model

import cc.dvitski.direbats.Direbats
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.resources.ResourceLocation

/**
 * Represents Direbats entity model layers.
 */
@Environment(EnvType.CLIENT)
object DirebatsEntityModelLayers {
    val DIREBAT = registerMain("direbat")

    private fun registerMain(id: String): ModelLayerLocation {
        val layer = ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Direbats.MOD_ID, id), "main")
        EntityModelLayerRegistry.registerModelLayer(layer, DirebatEntityModel::TEXTURED_MODEL_DATA)
        return layer
    }
}
