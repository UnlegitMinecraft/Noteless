package net.ccbluex.liquidbounce.features.module.modules.player


import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import java.util.*
import java.util.Timer

@ModuleInfo(name = "AutoPit", description = "Automatically equips the best armor in your inventory.", category = ModuleCategory.MISC)
class AutoPit : Module() {
    val timer = Timer()
    var check = true
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer.health < 5) {
            for (i in 0..3) {
                val armorSlot = 3 - i
                move(8 - armorSlot, true)
            }
            if(check == true) {
                check = false
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        mc.thePlayer.sendChatMessage("/hub")
                    }
                }
                    , 50)
            }
        }

    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    private fun move(item: Int, isArmorSlot: Boolean) {
        if (item != -1) {
            val openInventory = mc.currentScreen !is GuiInventory
            if (openInventory) mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, if (isArmorSlot) item else if (item < 9) item + 36 else item, 0, 1, mc.thePlayer)
            if (openInventory) mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent?) {
        check = true
    }
}