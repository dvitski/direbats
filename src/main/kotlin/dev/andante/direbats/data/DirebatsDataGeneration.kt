package dev.andante.direbats.data

import dev.andante.direbats.data.server.DirebatsAdvancementProvider
import dev.andante.direbats.data.server.DirebatsEntityLootTableProvider
import dev.andante.direbats.data.server.DirebatsLanguageProvider
import dev.andante.direbats.data.server.DirebatsRecipeProvider
import dev.andante.direbats.data.server.tag.DirebatsBiomeTagProvider
import dev.andante.direbats.data.server.tag.DirebatsEntityTypeTagProvider
import dev.andante.direbats.data.server.tag.DirebatsGameEventTagProvider
import dev.andante.direbats.data.server.tag.DirebatsItemTagProvider
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.InvocationTargetException


/**
 * Generates Direbats assets and data.
 */
object DirebatsDataGeneration : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = generator.createPack()

        pack.addProvider(::DirebatsLanguageProvider)

        pack.addProvider(::DirebatsAdvancementProvider)
        pack.addProvider(::DirebatsEntityLootTableProvider)
        pack.addProvider(::DirebatsRecipeProvider)

        pack.addProvider(::DirebatsBiomeTagProvider)
        pack.addProvider(::DirebatsEntityTypeTagProvider)
        pack.addProvider(::DirebatsGameEventTagProvider)
        pack.addProvider(::DirebatsItemTagProvider)

        // TODO replace with a client only entrypoint with FMJ 2
        if (FabricLoader.getInstance().environmentType === EnvType.CLIENT) {
            try {
                val clientEntrypointClass = Class.forName("dev.andante.direbats.client.data.DirebatsClientDataGeneration")
                val entrypoint = clientEntrypointClass.getConstructor().newInstance() as DataGeneratorEntrypoint
                entrypoint.onInitializeDataGenerator(generator)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            } catch (e: InstantiationException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }
        }
    }
}
