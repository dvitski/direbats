package cc.dvitski.direbats.client

import cc.dvitski.direbats.client.render.entity.DirebatEntityRenderer
import cc.dvitski.direbats.client.render.entity.DirebatFangArrowEntityRenderer
import cc.dvitski.direbats.client.render.entity.model.DirebatsEntityModelLayers
import cc.dvitski.direbats.entity.DirebatsEntityTypes
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.entity.EntityRenderers

@Environment(EnvType.CLIENT)
object DirebatsClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerEntityRenderers()
    }

    private fun registerEntityRenderers() {
        DirebatsEntityModelLayers

        EntityRenderers.register(DirebatsEntityTypes.DIREBAT, ::DirebatEntityRenderer)
        EntityRenderers.register(DirebatsEntityTypes.DIREBAT_FANG_ARROW, ::DirebatFangArrowEntityRenderer)
    }
}
