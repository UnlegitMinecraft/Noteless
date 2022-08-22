package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.misc.SoundFxPlayer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.IChatComponent
import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoGG", category = ModuleCategory.PLAYER,description = "idk")
class AutoPlay : Module() {
    private var clickState = 0
    private val modeValue = ListValue("Server", arrayOf("RedeSky", "BlocksMC", "Minemora", "Hypixel", "HuaYuTingGG"), "HuaYuTingGG")
    private val delayValue = IntegerValue("JoinDelay", 3, 0, 7)

    private var clicking = false
    private var queued = false
    override fun onEnable() {
        clickState = 0
        clicking = false
        queued = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().toLowerCase()) {
            "redesky" -> {
                if (clicking && (packet is C0EPacketClickWindow || packet is C07PacketPlayerDigging)) {
                    event.cancelEvent()
                    return
                }
                if (clickState == 2 && packet is S2DPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
            "hypixel" -> {
                if (clickState == 1 && packet is S2DPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
        }

        if (packet is S2FPacketSetSlot) {
            val item = packet.func_149174_e() ?: return
            val windowId = packet.func_149175_c()
            val slot = packet.func_149173_d()
            val itemName = item.unlocalizedName
            val displayName = item.displayName

            when (modeValue.get().toLowerCase()) {
                "redesky" -> {
                    if (clickState == 0 && windowId == 0 && slot == 42 && itemName.contains("paper", ignoreCase = true) && displayName.contains("Jogar novamente", ignoreCase = true)) {
                        clickState = 1
                        clicking = true
                        queueAutoPlay {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(6))
                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                            clickState = 2
                        }
                    } else if (clickState == 2 && windowId != 0 && slot == 11 && itemName.contains("enderPearl", ignoreCase = true)) {
                        Timer().schedule(500L) {
                            clicking = false
                            clickState = 0
                            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                        }
                    }
                }
                "blocksmc", "hypixel" -> {
                    if (clickState == 0 && windowId == 0 && slot == 43 && itemName.contains("paper", ignoreCase = true)) {
                        queueAutoPlay {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
                            repeat(2) {
                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
                            }
                        }
                        clickState = 1
                    }
                    if (modeValue.equals("hypixel") && clickState == 1 && windowId != 0 && itemName.equals("item.fireworks", ignoreCase = true)) {
                        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                        mc.netHandler.addToSendQueue(C0DPacketCloseWindow(windowId))
                    }
                }
            }
        } else if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().toLowerCase()) {
                "minemora" -> {
                    if (text.contains("Has click en alguna de las siguientes opciones", true)) {
                        queueAutoPlay {
                            mc.thePlayer.sendChatMessage("/join")
                        }
                    }
                }
                "blocksmc" -> {
                    if (clickState == 1 && text.contains("Only VIP players can join full servers!", true)) {
                        LiquidBounce.hud.addNotification(Notification(name,"Join failed try again", NotifyType.WARNING))
                        // connect failed so try to join again
                        Timer().schedule(1500L) {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
                            repeat(2) {
                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                            }
                        }
                    }
                }
                "huayutinggg" -> {
                    if (text.contains("      喜欢      一般      不喜欢", true)) {
                        LiquidBounce.hud.addNotification(Notification(name,"Game Over", NotifyType.INFO))
                        mc.thePlayer.sendChatMessage("@[Noteless]GG")
                    }
                }
                "hypixel" -> {
                    fun process(component: IChatComponent) {
                        val value = component.chatStyle.chatClickEvent?.value
                        if (value != null && value.startsWith("/play", true)) {
                            mc.thePlayer.sendChatMessage("/ac [Noteless]GG Hacked By Dimples#1337")
                            queueAutoPlay {
                                mc.thePlayer.sendChatMessage(value)
                            }
                        }
                        component.siblings.forEach {
                            process(it)
                        }
                    }
                    process(packet.chatComponent)
                }
            }
        }
    }

    private fun queueAutoPlay(runnable: () -> Unit) {
        if (queued) {
            return
        }
        queued = true
        if (this.state) {
            Timer().schedule(delayValue.get().toLong() * 1000) {
                if (state) {
                    runnable()
                }
            }

            //play sound when everything done
            SoundFxPlayer().playSound(SoundFxPlayer.SoundType.VICTORY, -8f)
            LiquidBounce.hud.addNotification(Notification(name,"Sending you to next game in ${delayValue.get()}s...", NotifyType.INFO))
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clicking = false
        clickState = 0
        queued = false
    }

    override val tag: String
        get() = modeValue.get()

    override fun handleEvents() = true
}
