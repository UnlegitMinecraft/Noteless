package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus

@ModuleInfo(name = "AutoPit", description = "Hyt AutoHub", category = ModuleCategory.COMBAT)
class AutoLobby : Module(){
    var health = FloatValue("Health", 5F, 0F, 20F)
    var canhubchat = BoolValue("CanHubChat",false)
    var hubchattext = TextValue("AutoHubChatText","You IS L")
    var disabler = BoolValue("AutoDisable-KillAura-Velocity", true)
    var keepArmor = BoolValue("KeepArmor", true)


    fun getRandomNumber(): Int { //取1-100随机数
        val low = 1
        val high = 100

        val n = ((Math.random().toInt() * (high - low))) + low
        return n
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        val velocity = LiquidBounce.moduleManager[Velocity::class.java] as Velocity
        if (mc.thePlayer.health < health.get()){
            if(keepArmor.get()) {
                for (i in 0..3) {
                    val armorSlot = 3 - i
                    move(8 - armorSlot, true)
                }
            }
            if(canhubchat.get()){
                mc.thePlayer.sendChatMessage(hubchattext.get())
            }
            mc.thePlayer.sendChatMessage("/hub " + getRandomNumber())
            if (disabler.get()){
                killAura.state = false
                velocity.state = false
            }
        }
    }

    private fun move(item: Int, isArmorSlot: Boolean) { //By Gk
        if (item != -1) {
            val openInventory = mc.currentScreen !is GuiInventory
            if (openInventory) mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, if (isArmorSlot) item else if (item < 9) item + 36 else item, 0, 1, mc.thePlayer)
            if (openInventory) mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
        }
    }

    override val tag: String?
        get() =  "HuaYuTingTianKeng"
}
