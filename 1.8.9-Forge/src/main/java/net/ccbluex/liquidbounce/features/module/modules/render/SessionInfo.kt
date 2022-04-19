package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Arraylist
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Recorder
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Translate
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.network.play.server.S45PacketTitle


import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

@ModuleInfo(name = "SessionInfo", category = ModuleCategory.RENDER, description="kill youer mother")
class SessionInfo : Module() {
    private val blurValue = BoolValue("blur", true)
    private val xValue = FloatValue("xValue", 100f, 0f, 500f)
    private val yValue = FloatValue("yValue", 100f, 0f, 500f)
    private val r = IntegerValue("R", 0, 0, 255)
    private val g = IntegerValue("G", 111, 0, 255)
    private val b = IntegerValue("B", 255, 0, 255)
    private val a = IntegerValue("alpha", 255, 0, 255)
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
    var syncEntity: EntityLivingBase? = null
    var killCounts = 0
    var totalPlayed = 0
    var win = 0
    var startTime = System.currentTimeMillis()
    @EventTarget
    private fun OnRender2D(event: Render2DEvent){
        pushMatrix()
        if(blurValue.get()) {BlurUtils.blurArea(xValue.get(),yValue.get()+10F, xValue.get()+120F, yValue.get()+75F, 20F)}
        RenderUtils.drawCircleRect( xValue.get(), yValue.get()+10F, xValue.get()+120F, yValue.get()+75F, 7f,Color(0, 0, 0, 50).rgb)
        Fonts.flux.drawString("F", xValue.get()+5F, yValue.get()+16F, -1)
        Fonts.font30.drawString("Play Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}", xValue.get()+20F, yValue.get()+15F, -1)
        Fonts.flux.drawString("G", xValue.get()+5F, yValue.get()+31F, -1)
        Fonts.font30.drawString("Kills: $killCounts", xValue.get()+20F, yValue.get()+30F, -1)
        Fonts.flux.drawString("H", xValue.get()+5F, yValue.get()+46F, -1)
        Fonts.font30.drawString("Win / Total: $win / $totalPlayed", xValue.get()+20F, yValue.get()+45F, -1)
        Fonts.flux.drawString("I", xValue.get()+5F, yValue.get()+61F, -1)
        Fonts.font30.drawString("Speed:"+MovementUtils.getBlockSpeed(mc.thePlayer)+"/bps", xValue.get()+20F, yValue.get()+60F, -1)
        popMatrix()
    }
    @EventTarget
    private fun onAttack(event: AttackEvent) { syncEntity = event.targetEntity as EntityLivingBase?
    }
    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        if(syncEntity != null && syncEntity!!.isDead) {
            ++killCounts
            syncEntity = null
        }
    }
    @EventTarget(ignoreCondition = true)
    private fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (event.packet is C00Handshake) startTime = System.currentTimeMillis()

        if (packet is S45PacketTitle) {
            val title = packet.message.formattedText
            if(title.contains("CHAMPION")){
                win++
            }
            if(title.contains("BedWar")){
                totalPlayed++
            }
            if(title.contains("SkyWar")){
                totalPlayed++
            }
        }
    }
}
