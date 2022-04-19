package Insane.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.RenderU.drawTriAngle

import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MathHelper
import java.awt.Color
import kotlin.math.*

@ModuleInfo(
    name = "Arrows",
    description = "Show the pointers around your crossing.",
    category = ModuleCategory.RENDER
)
class PointerESP : Module() {
    private val solidValue = BoolValue("Solid", value = false)
    private val redValue = IntegerValue("Red",140,0,255)
    private val greenValue = IntegerValue("Green",140,0,255)
    private val blueValue = IntegerValue("Blue",255,0,255)
    private val alphaValue = IntegerValue("Alpha",255,0,255)
    @EventTarget
    fun onRender2D(event : Render2DEvent) {
        val a = ScaledResolution(mc)
        GlStateManager.pushMatrix()
        val size = 50
        val xOffset = a.scaledWidth / 2 - 24.5
        val yOffset = a.scaledHeight / 2 - 25.2
        val playerOffsetX = mc.thePlayer.posX
        val playerOffSetZ = mc.thePlayer.posZ
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityPlayer && entity != mc.thePlayer) {
                val pTicks = mc.timer.renderPartialTicks
                val pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * pTicks) - playerOffsetX) * 0.2)
                val pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * pTicks) - playerOffSetZ) * 0.2)
                val cos = cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360))
                val sin = sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360))
                val rotY = -(pos2 * cos - pos1 * sin)
                val rotX = -(pos1 * cos + pos2 * sin)
                val var7 = 0 - rotX
                val var9 = 0 - rotY
                val color = Color(redValue.get(),greenValue.get(),blueValue.get(),alphaValue.get()).rgb
                if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2 - 4) {
                    val angle = (atan2(rotY - 0, rotX - 0) * 180 / Math.PI).toFloat()
                    val x = ((size / 2) * cos(Math.toRadians(angle.toDouble()))) + xOffset + size / 2;
                    val y = ((size / 2) * sin(Math.toRadians(angle.toDouble()))) + yOffset + size / 2;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, 0.0)
                    GlStateManager.rotate(angle, 0F, 0F, 1F)
                    GlStateManager.scale(1.5, 1.0, 1.0)
                    if (solidValue.get()){
                        drawTriAngle(0F, 0F, 2.2F, 3F, color)
                        drawTriAngle(0F, 0F, 1.5F, 3F, color)
                        drawTriAngle(0F, 0F, 1.0F, 3F, color)
                        drawTriAngle(0F, 0F, 0.5F, 3F, color)
                    } else
                        drawTriAngle(0F, 0F, 2.2F, 3F, color)
                    GlStateManager.popMatrix()
                }
            }
        }
        GlStateManager.popMatrix()
    }
}