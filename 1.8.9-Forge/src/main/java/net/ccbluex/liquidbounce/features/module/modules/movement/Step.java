package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.StepConfirmEvent;
import net.ccbluex.liquidbounce.event.StepEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import static net.ccbluex.liquidbounce.utils.PacketUtils.sendPacketNoEvent;

@ModuleInfo(name = "Step",description = "Hi",category = ModuleCategory.MOVEMENT)
public class Step extends Module {
    private FloatValue timer = new FloatValue("Timer",0.7f,0.1f,1f);
    private boolean resetTimer;

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.stepHeight = 0.625F;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.stepHeight =  !mc.thePlayer.onGround ? 0.625F : 1.0F;
        if (resetTimer) {
            mc.timer.timerSpeed = 1.0F;
            resetTimer = false;
        }
    }
    //            if (event.state && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
//                event.stepHeight = 1.0f
//            } else if (!event.state && event.realHeight > 0.5 && event.stepHeight > 0.0 && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
//                stepping = true
//                if (event.realHeight >= 0.87) {
//                    val realHeight: Double = event.realHeight
//                    val height1 = realHeight * 0.42
//                    val height2 = realHeight * 0.75
//                    mc.thePlayer.sendQueue.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height1, mc.thePlayer.posZ, mc.thePlayer.onGround))
//                    mc.thePlayer.sendQueue.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height2, mc.thePlayer.posZ, mc.thePlayer.onGround))
//                }
//                mc.timer.timerSpeed = 0.55f
//                Thread(Runnable {
//                    try {
//                        Thread.sleep(100L)
//                    } catch (var1: InterruptedException) {
//                    }
//                    stepping = false
//                    mc.timer.timerSpeed = 1.0f
//                }).start()
//            }
    @EventTarget
    public void onStep(StepEvent event) {
        double diffY = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
        double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;

        if (diffY > 0.625 && diffY <= 1.0) {
            sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            mc.thePlayer.setSprinting(false);
            MovementUtils.setMotion(0);

            mc.timer.timerSpeed = timer.get();
            resetTimer = true;

            //double first = 0.41999998688698, second = 0.7531999805212;
            double realHeight = event.getRealHeight();
            double first = realHeight * 0.42;
            double second  = realHeight * 0.75;
//            if (diffY != 1) {
//                first *= diffY;
//                second *= diffY;
//
//                if (first > 0.425) {
//                    first = 0.425;
//                }
//
//                if (second > 0.78) {
//                    second = 0.78;
//                } else if (second < 0.49) {
//                    second = 0.49;
//                }
//            }

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + first, posZ, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + second, posZ, false));

            sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));

            mc.thePlayer.stepHeight = 0.625f;
        }
    }

    @Override
    public String getTag() {
        return "Hypixel";
    }
}
