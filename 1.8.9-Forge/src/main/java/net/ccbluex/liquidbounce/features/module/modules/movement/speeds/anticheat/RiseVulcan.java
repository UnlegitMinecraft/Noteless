package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class RiseVulcan extends SpeedMode {

        public RiseVulcan() {
            super("RiseVulcan");
        }
    private int offGroundTicks;
    @Override
    public void onEnable() {
        offGroundTicks = 0;
    }
        @Override
        public void onMotion() {
            if (mc.thePlayer.onGround) {
                offGroundTicks = 0;
            } else {
                offGroundTicks += 1;
            }
            if (mc.thePlayer.onGround&& mc.thePlayer.motionY > -.2) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition((mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2, ((mc.thePlayer.posY + mc.thePlayer.lastTickPosY) / 2) - 0.0784000015258789, (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook((mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2, (mc.thePlayer.posY + mc.thePlayer.lastTickPosY) / 2, (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));

                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784000015258789, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));

                MovementUtils.strafe((float) (MovementUtils.getBaseMoveSpeed() * 1.25 * 2));

            } else if (offGroundTicks == 1) {
                MovementUtils.strafe((float) (MovementUtils.getBaseMoveSpeed() * 0.91f));
            }
        }


}
