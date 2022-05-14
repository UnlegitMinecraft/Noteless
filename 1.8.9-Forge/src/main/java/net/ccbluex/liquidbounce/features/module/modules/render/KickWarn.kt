package net.ccbluex.liquidbounce.features.module.modules.render


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Translate
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C03PacketPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "KickWarn", description = "shabi.", category = ModuleCategory.RENDER)
class KickWarn : Module() {
    private val xValue = IntegerValue("X", 375, -500, 500)
    private val yValue = IntegerValue("Y", 125, -500, 500)
    private val packetsCheck = BoolValue("PacketsCheck", false)
    private val vanillaCheck = BoolValue("VanillaCheck", true)
    private var translat = Translate(0f , 0f)
    private var counter = 0
    private var lastCounter = 0
    private var tick0 = 0
    private var tick1 = 0
    private var tick2 = 0

    @EventTarget
    fun onPacket(e: PacketEvent) {
        val packet = e.packet

        if (packetsCheck.get()) {
            if (packet is C03PacketPlayer) {
                counter++
            }
        }
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        if (packetsCheck.get()) {
            tick0++
            if (tick0 == 20) {
                lastCounter = (counter * mc.timer.timerSpeed).toInt()
                counter = 0
                tick0 = 0
            }

            if (lastCounter > 22) {
                tick1++
            } else {
                tick1 = 0
            }
        }

        if (vanillaCheck.get()) {
            if (inAir) {
                tick2++
            } else {
                tick2 = 0
            }
        }
    }

    @EventTarget
    fun onRender2D(e: Render2DEvent) {
        val x = RenderUtils.width() / 2f + xValue.get()
        val y = RenderUtils.height() / 2f + yValue.get()
        val width = x + 100

        GL11.glPushMatrix()
        val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
        val colors = arrayOf(Color(100, 225, 100), Color(225, 225, 100), Color(225, 75, 75))

        translat.translate(tick1.toFloat() , tick2.toFloat() , 0.5)

        // TooMany Packets
        if (packetsCheck.get()) {
            Fonts.SFUI40.drawString(
                "TooMany Packets",
                x + (100f - Fonts.SFUI40.getStringWidth("TooMany Packets")) / 2f,
                y,
                Color(255, 255, 255, 200).rgb,
                false
            )
            RenderUtils.drawRect(x, y + 10f, width, y + 25f, Color(0, 0, 0, 125))
            RenderUtils.drawRect(
                x,
                y + 10f,
                Math.min(x + translat.x, width),
                y + 25f,
                Colors.blendColors(fractions, colors, translat.x / 50f)
            )
        }

        // Vanilla Kick
        if (vanillaCheck.get()) {
            Fonts.SFUI40.drawString(
                "Vanilla Kick",
                x + (100f - Fonts.SFUI40.getStringWidth("Vanilla Kick")) / 2f,
                y + 35f,
                Color(255, 255, 255, 200).rgb,
                false
            )
            RenderUtils.drawRect(x, y + 45f, width, y + 60f, Color(0, 0, 0, 125))
            RenderUtils.drawRect(
                x,
                y + 45f,
                Math.min(x + translat.y, width),
                y + 60f,
                Colors.blendColors(fractions, colors, translat.y / 50f)
            )
        }

        GL11.glPopMatrix()
    }

    private val inAir: Boolean
        get() = !MovementUtils.isOnGround(0.001) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb && LiquidBounce.moduleManager.getModule(Fly::class.java)!!.state
}
