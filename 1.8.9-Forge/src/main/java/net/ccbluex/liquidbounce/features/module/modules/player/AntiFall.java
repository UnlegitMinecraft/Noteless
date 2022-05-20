package net.ccbluex.liquidbounce.features.module.modules.player;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import java.util.ArrayList;

@ModuleInfo(name = "AntiFall", description = "Antifall.", category = ModuleCategory.PLAYER)
public class AntiFall extends Module {
    public double[] lastGroundPos = new double[3];
    public static FloatValue pullbackTime = new FloatValue("Pullback Time", 1500f, 1000f, 2000f);
    public static TimerUtil timer = new TimerUtil();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MovementUtils.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }

    @EventTarget
    public void onPacket(PacketEvent e) {
            if (LiquidBounce.moduleManager.get(Fly.class).getState() || LiquidBounce.moduleManager.get(Scaffold.class).getState())
                return;

            if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100)
                packets.clear();

            if (e.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());
                if (isInVoid()) {
                    e.cancelEvent();
                    packets.add(packet);

                    if (timer.delay(pullbackTime.get())) {
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                    }
                } else {
                    lastGroundPos[0] = mc.thePlayer.posX;
                    lastGroundPos[1] = mc.thePlayer.posY;
                    lastGroundPos[2] = mc.thePlayer.posZ;

                    if (!packets.isEmpty()) {
                        for (C03PacketPlayer p : packets)
                            PacketUtils.sendPacketNoEvent(p);
                        packets.clear();
                    }
                    timer.reset();
                }
            }
    }

    @EventTarget
    public void onRevPacket(PacketEvent e) {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && packets.size() > 1) {
                packets.clear();
        }
    }

    public static boolean isPullbacking() {
        return LiquidBounce.moduleManager.get(AntiFall.class).getState() && !packets.isEmpty();
    }

    @Override
    public String getTag() {
        return pullbackTime.get().toString();
    }
}
