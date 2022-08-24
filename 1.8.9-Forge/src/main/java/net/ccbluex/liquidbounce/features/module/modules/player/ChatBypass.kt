package skidunion.destiny.module.impl.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.client.C01PacketChatMessage


@ModuleInfo(name = "ChatBypass", description = "", category = ModuleCategory.PLAYER)
class ChatBypass : Module() {

    @EventTarget
    fun onUpdate(event: PacketEvent) {
        mc.thePlayer ?: return
        mc.theWorld ?: return
        val packet = event.packet
        if (packet is C01PacketChatMessage) {
            val message = packet.getMessage()
            if(message.startsWith("/")) return
            val stringBuilder = StringBuilder()
            message.toCharArray().forEach { char ->
                if(char.toInt() in 33..128) stringBuilder.append(Character.toChars(char.toInt() + 65248))
                else stringBuilder.append(char)
            }
            packet.message = stringBuilder.toString()
        }
    }
    override val tag: String
        get() = "Instant"
}