package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S3FPacketCustomPayload

@ModuleInfo(name = "AutoReport", category = ModuleCategory.HYT,description = "dog")
class AutoReport : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Hit", "All"), "Hit")
    private val commandValue = TextValue("Command", "/report %name%")
    private val tipValue = BoolValue("Tip", true)
    private val allDelayValue = IntegerValue("AllDelay", 500, 0, 1000)
    private val blockBooksValue = BoolValue("BlockBooks", false) // 绕过Hypixel /report举报弹出书

    private val reported = mutableListOf<String>()
    private val delayTimer = MSTimer()

    override fun onEnable() {
        reported.clear()
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val entity = event.targetEntity ?: return
        if (isTarget(entity)) {
            doReport(entity as EntityPlayer)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.equals("All") && delayTimer.hasTimePassed(allDelayValue.get().toLong())) {
            mc.netHandler.playerInfoMap.forEach {
                val name = it.gameProfile.name
            }
            delayTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (blockBooksValue.get() && event.packet is S3FPacketCustomPayload) {
            event.cancelEvent()
        }
    }

    fun doReport(player: EntityPlayer) = doReport(player.name)

    fun doReport(name: String): Boolean {
        // pass this if reported
        if (reported.contains(name)) {
            return false
        }

        reported.add(name)
        mc.thePlayer.sendChatMessage(commandValue.get().replace("%name%", name))
        if (tipValue.get()) {
            ClientUtils.displayChatMessage("$name reported!")
        }
        return true
    }

    private fun isTarget(entity: Entity): Boolean {
        if (entity is EntityPlayer) {
            if (entity == mc.thePlayer) {
                return false
            }

            if (AntiBot.isBot(entity)) {
                return false
            }

            if (EntityUtils.isFriend(entity)) {
                return false
            }

            if (entity.isSpectator) {
                return false
            }
        }

        return false
    }

    override val tag: String
        get() = modeValue.get()
}