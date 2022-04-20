package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class TargetHud : Element() {
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val gredValue = IntegerValue("GradientRed", 255, 0, 255)
    private val ggreenValue = IntegerValue("GradientGreen", 255, 0, 255)
    private val gblueValue = IntegerValue("GradientBlue", 255, 0, 255)
    private val fontValue = FontValue("Font", Fonts.minecraftFont)
    val fontRenderer = fontValue.get()
    private var easingHealth: Float = 0F
    private var lastTarget: Entity? = null
    val counter1 = intArrayOf(50)
    val counter2 = intArrayOf(80)
    override fun drawElement(): Border? {
        val target:EntityLivingBase?
        if (mc.currentScreen is GuiChat || mc.currentScreen is GuiHudDesigner) {
            target = mc.thePlayer
        } else {
            target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        }
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
        if (target is EntityPlayer) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }
            val width = (38 + Fonts.font35.getStringWidth(target.name))
                .coerceAtLeast(118)
                .toFloat()

            RenderUtils.drawRect(0F, 0F, width + 3F,34.5F, Color(0,0,0,150))
            RenderUtils.drawRect(-1.5F, -1.5F, width + 4.5F,36F, Color(50,50,50,200))
            val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
            val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
            RenderUtils.drawGradientSideways(36.0, 16.0, width.toDouble(),
                24.0, Color(40,40,40,220).rgb, Color(60,60,60,255).rgb)
            RenderUtils.drawGradientSideways(36.0, 16.0, (36.0F + (easingHealth / target.maxHealth) * (width - 36.0F)).toDouble(),
                24.0, Palette.fade2(customColor,counter1[0], fontRenderer.FONT_HEIGHT).rgb,Palette.fade2(customColor1, counter2[0], fontRenderer.FONT_HEIGHT).rgb)
            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                fontRenderer.drawString(target.name, 36, 4, Color(255,255,255,200).rgb)
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)
            }
            fontRenderer.drawStringWithShadow(BigDecimal((target.health / target.maxHealth * 100).toDouble()).setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "%", width / 2F + 6.5F, 16F, Color.white.rgb)
        }

        lastTarget = target
        val width2 = (38 + Fonts.font35.getStringWidth(mc.thePlayer.name))
            .coerceAtLeast(118)
            .toFloat()
        return Border(0F, 0F, width2 + 3F,34.5F)
    }


    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F)
    }
}