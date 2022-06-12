package net.ccbluex.liquidbounce.features.module.modules.player;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

import static net.ccbluex.liquidbounce.utils.PacketUtils.sendPacketNoEvent;

@ModuleInfo(name = "NoFall", description = "", category = ModuleCategory.PLAYER)
public final class NoFall extends Module {





    private double getLastTickYDistance() {
        return Math.hypot(mc.thePlayer.posY - mc.thePlayer.prevPosY, mc.thePlayer.posY - mc.thePlayer.prevPosY);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
                if (mc.thePlayer.posY > 0 && mc.thePlayer.fallDistance >= 2 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0) {
                    if (!MovementUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MovementUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                        return;
                    }

                    if (event.getPacket() instanceof C02PacketUseEntity) {
                        C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

                        if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                            event.cancelEvent();
                        }
                    }

                    if (event.getPacket() instanceof C03PacketPlayer) {
                        C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                        if (packet.isMoving() && packet.rotating) {
                            sendPacketNoEvent(new C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.isOnGround()));
                            event.cancelEvent();
                        }
                    }
                }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (mc.thePlayer.posY > 0 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.fallDistance >= 2.5) {
                if (!MovementUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MovementUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                    return;
                }

                if (mc.thePlayer.fallDistance > 10 || mc.thePlayer.ticksExisted % 2 == 0) {
                    sendPacketNoEvent(new C03PacketPlayer(true));
                    mc.timer.timerSpeed = 1.0F;
                }
            }
        }
    }
    @Override
    public String getTag() {
        return "Hypixel";
    }

}
