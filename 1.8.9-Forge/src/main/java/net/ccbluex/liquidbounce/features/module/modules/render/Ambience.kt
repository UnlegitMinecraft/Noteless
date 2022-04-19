package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.S03PacketTimeUpdate

@ModuleInfo(
        name = "Ambience",
        description = "Change the World Time.",
        category = ModuleCategory.MISC
)
class Ambience : Module() {
    var i : Long = 0
    private val modeValue = ListValue("Mode", arrayOf("Normal", "Custom"), "Normal")
    private val customWorldTimeValue = IntegerValue("CustomTime", 1000, 0, 24000)
    private val changeWorldTimeSpeedValue = IntegerValue("ChangeWorldTimeSpeed", 150, 10, 500)
    override fun onDisable() {
        i = 0
    }

    override val tag: String
        get() = modeValue.get()
    @EventTarget
    fun onUpdate(event : UpdateEvent) {
        when (modeValue.get()) {
            "Normal" -> {
                if (i < 24000)
                    i += changeWorldTimeSpeedValue.get()
                 else
                    i = 0
                mc.theWorld.worldTime = i
            }
            "Custom" -> {
                mc.theWorld.worldTime = customWorldTimeValue.get().toLong()
            }
        }
    }
}