package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "PlayerEdit", description = "Edit the player.", category = ModuleCategory.PLAYER)
public class PlayerEdit extends Module {
    public static FloatValue playerSizeValue = new FloatValue("PlayerSize", 0.5f,0.01f,5f);
    public static BoolValue editPlayerSizeValue = new BoolValue("EditPlayerSize", true);
}
