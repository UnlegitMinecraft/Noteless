package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S14PacketEntity
import java.util.regex.Pattern

@ModuleInfo(name = "HYTGetName", description = "idk", category = ModuleCategory.MISC)
class GetName : Module() {
    private val playerName: MutableList<String> = ArrayList()
    val ground = mutableListOf<Int>()
    val modeValue = ListValue("Mode", arrayOf("4V4/1V1","32V32/64V64"),"4V4/1V1")
    override fun onDisable() {
        clearAll()
        super.onDisable()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S02PacketChat) {
            val chatMessage = packet
            val matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(chatMessage.chatComponent.getUnformattedText())
            val matcher2 = Pattern.compile("> (.*?)\\(").matcher(chatMessage.chatComponent.getUnformattedText())
            val friendsConfig = LiquidBounce.fileManager.friendsConfig

            if (matcher.find()) {
                val name = matcher.group(1)
                if (name != "") {
                    if (!playerName.contains(name)) {
                        playerName.add(name)
                        friendsConfig.addFriend(name)
                        ClientUtils.displayChatMessage("§7[§8§lNoteless§7]§fDeleted HYT Bot:$name")
                        Thread {
                            try {
                                Thread.sleep(6000)
                                playerName.remove(name)
                                friendsConfig.removeFriend(name)
                                ClientUtils.displayChatMessage("§7[§8§lNoteless§7]§fDeleted HYT Bot:$name")
                            } catch (ex: InterruptedException) {
                                ex.printStackTrace()
                            }
                        }.start()
                    }
                }
            }
            if (matcher2.find()) {
                val name = matcher2.group(1)
                if (name != "" && !name.contains("[")) {
                    if (!playerName.contains(name)) {
                        playerName.add(name)
                        friendsConfig.addFriend(name)
                        ClientUtils.displayChatMessage("§7[§8§lNoteless§7]§fDeleted HYT Bot:$name")
                        Thread {
                            try {
                                Thread.sleep(6000)
                                playerName.remove(name)
                                friendsConfig.removeFriend(name)
                                ClientUtils.displayChatMessage("§7[§8§lNoteless§7]§fDeleted HYT Bot:$name")
                            } catch (ex: InterruptedException) {
                                ex.printStackTrace()
                            }
                        }.start()
                    }
                }
            }
        }
        if (modeValue.get() == "4V4/1V1") {
            if (packet is S14PacketEntity) {
                val packetEntity = packet
                val entity = packetEntity.getEntity(mc.theWorld!!)

                if (entity is EntityPlayer && entity != null) {
                    if (packetEntity.onGround && !ground.contains(entity.entityId))
                        ground.add(entity.entityId)
                }
            }
        }
        if (modeValue.get() == "32V32/64V64") {
            if (packet is S14PacketEntity) {
                val packetEntity = packet
                val entity = packetEntity.getEntity(mc.theWorld!!)

                if (entity is EntityPlayer && entity != null) {
                    if (packetEntity.onGround && !ground.contains(entity.entityId))
                        ground.add(entity.entityId)
                }
            }
        }
    }
    @EventTarget
    fun onWorld(event: WorldEvent?) {
        clearAll()
    }

    private fun clearAll() {
        playerName.clear()
    }
    companion object {
        @JvmStatic
        fun isBot(entity: EntityLivingBase): Boolean {
            if (entity !is EntityPlayer) return false
            val antiBot = LiquidBounce.moduleManager.getModule(GetName::class.java) as GetName?
            if (antiBot == null || !antiBot.state) return false
            if (antiBot.modeValue.get() == "4V4/1V1" && !antiBot.ground.contains(entity.entityId)) return true
            if (antiBot.modeValue.get() == "4V4/1V1" && entity.ticksExisted < 18) return true
            if (antiBot.modeValue.get() == "32V32/64V64" && !antiBot.ground.contains(entity.entityId)) return true
            if (antiBot.modeValue.get() == "4V4/1V1") {
                val player = entity
                if (player.inventory.armorInventory[0] == null && player.inventory.armorInventory[1] == null && player.inventory.armorInventory[2] == null && player.inventory.armorInventory[3] == null) return true
            }
            return entity.name!!.isEmpty() || entity.name == mc.thePlayer!!.name
        }
    }
}