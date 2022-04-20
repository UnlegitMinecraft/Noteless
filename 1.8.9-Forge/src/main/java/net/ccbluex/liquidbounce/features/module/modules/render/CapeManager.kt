package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager

@ModuleInfo(name = "CapeManager", description = "CapeManager.", category = ModuleCategory.RENDER, canEnable = false)
class CapeManager : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(GuiCapeManager)
    }
}