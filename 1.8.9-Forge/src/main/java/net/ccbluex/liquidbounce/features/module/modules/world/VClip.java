package net.ccbluex.liquidbounce.features.module.modules.world;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "VClip", description = "Clip.", category = ModuleCategory.WORLD)
public class VClip extends Module {

    public final IntegerValue clips = new IntegerValue("Down Value", 4, 1, 10);

    @EventTarget
    public void onUpdate(UpdateEvent e) {
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - this.clips.get(), mc.thePlayer.posZ, true));
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - this.clips.get(), mc.thePlayer.posZ);
    }
}
