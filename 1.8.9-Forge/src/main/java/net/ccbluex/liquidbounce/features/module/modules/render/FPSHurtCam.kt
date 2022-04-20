package net.ccbluex.liquidbounce.features.module.modules.render
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
@ModuleInfo(
    name = "FPSHurtCam",
    description = "Like fps games.",
    category = ModuleCategory.RENDER
)
class FPSHurtCam : Module() {
    var alpha = 0
    @EventTarget
    fun onRender2D(event : Render2DEvent) {
        val scaledResolution = ScaledResolution(mc)
        val width = scaledResolution.scaledWidth_double
        val height = scaledResolution.scaledHeight_double
        if (mc.thePlayer.hurtTime > 0) {
            if (alpha < 100) {
                alpha += 5
            }
        } else {
            if (alpha > 0) {
                alpha -= 5
            }
        }
        RenderUtils.drawGradientSidewaysV(0.0, 0.0, width, 25.0, Color(255, 0, 0, 0).rgb,
            Color(255, 0, 0, alpha).rgb)
        RenderUtils.drawGradientSidewaysV(0.0, height - 25, width, height,
            Color(255, 0, 0, alpha).rgb, Color(255, 0, 0, 0).rgb
        )
    }
}