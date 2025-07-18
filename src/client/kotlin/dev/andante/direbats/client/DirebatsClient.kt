package dev.andante.direbats.client

import dev.andante.direbats.client.render.entity.DirebatEntityRenderer
import dev.andante.direbats.client.render.entity.DirebatFangArrowEntityRenderer
import dev.andante.direbats.client.render.entity.model.DirebatsEntityModelLayers
import dev.andante.direbats.entity.DirebatsEntityTypes
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

@Environment(EnvType.CLIENT)
object DirebatsClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerEntityRenderers()
    }

    private fun registerEntityRenderers() {
        DirebatsEntityModelLayers

        EntityRendererRegistry.register(DirebatsEntityTypes.DIREBAT, ::DirebatEntityRenderer)
        EntityRendererRegistry.register(DirebatsEntityTypes.DIREBAT_FANG_ARROW, ::DirebatFangArrowEntityRenderer)
    }
}
