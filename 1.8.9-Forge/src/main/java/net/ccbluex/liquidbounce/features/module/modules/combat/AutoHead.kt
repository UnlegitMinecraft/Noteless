package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.potion.Potion

@ModuleInfo(name = "AutoHead", description = "Help u eat head and gapple when u low health", category = ModuleCategory.COMBAT)
class AutoHead : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Apple", "Head"), "Head")
    private val healthValue = FloatValue("Health", 15f, 0f, 20f)
    private val delayValue = IntegerValue("Delay", 150, 0, 500)
    private val RegenVaule = BoolValue("Swing", true)
    private val BothVaule = BoolValue("BothUse", false)

    private val timer = MSTimer()
    private var soup = -1
    var isUse = false

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        if (!timer.hasTimePassed(delayValue.get().toLong()) || mc.playerController.isInCreativeMode || (mc.thePlayer!!.openContainer != null && mc.thePlayer!!.openContainer!!.windowId != 0))
            return

        val thePlayer = mc.thePlayer ?: return

        when (event.eventState) {
            EventState.PRE -> {
                val appleInHotbar = InventoryUtils.findItem(36, 45, Items.golden_apple)

                val headInHotbar = InventoryUtils.findItem(36, 45, Items.skull)

                val potionREGENERATION = if(RegenVaule.get()) !mc.thePlayer!!.isPotionActive(Potion.regeneration) else true

                if(BothVaule.get() || modeValue.get().equals("Head")){
                    if (thePlayer.health <= healthValue.get() && headInHotbar != -1 && potionREGENERATION) {
                        if (thePlayer.itemInUseDuration < 24) {
                            soup = headInHotbar - 36
                            isUse = true
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(headInHotbar - 36))
                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(headInHotbar).stack)
                            )
                        }
                        timer.reset()
                        return
                    }
                }

                if(BothVaule.get() || modeValue.get().equals("Apple")){
                    if (thePlayer.health <= healthValue.get() && appleInHotbar != -1 && potionREGENERATION) {
                        if (thePlayer.itemInUseDuration < 24) {
                            soup = appleInHotbar - 36
                            isUse = true
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(appleInHotbar - 36))
                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(appleInHotbar).stack)
                            )
                        }
                        timer.reset()
                        return
                    }
                }

                if (soup != thePlayer.inventory.currentItem && isUse) {
                    isUse = false
                    soup = thePlayer.inventory.currentItem
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(thePlayer.inventory.currentItem))
                    return
                }
            }
        }
    }
}