package dev.andante.direbats.client.render.entity.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * Contains common model part names used in Direbats entity models.
 */
@Environment(EnvType.CLIENT)
object DirebatsEntityModelPartNames {
    const val LEFT_WING_OUTER = "left_wing_outer"
    const val RIGHT_WING_OUTER = "right_wing_outer"
    const val LEGS = "legs"
    const val TALONS = "talons"
    const val TAILBONE = "tailbone"
    const val FANGS = "fangs"
}
