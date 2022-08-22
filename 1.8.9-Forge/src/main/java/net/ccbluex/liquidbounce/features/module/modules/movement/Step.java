package net.ccbluex.liquidbounce.features.module.modules.movement;



import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.StepEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "Step", description = "Step", category = ModuleCategory.MOVEMENT)
public class Step extends Module {

    public static boolean stepping = false;
    public final BoolValue smooth = new BoolValue("smooth", false);

    @EventTarget
    public void onStep(StepEvent event) {
        if (LiquidBounce.moduleManager.get(Speed.class).getState() || LiquidBounce.moduleManager.get(Scaffold.class).getState())
            return;
        if (event.isPre() && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
            event.setStepHeight(1.0F);
        } else if (!event.isPre() && event.getRealHeight() > 0.5D && event.getStepHeight() > 0.0D && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
            stepping = true;
            if (event.getRealHeight() >= 0.87D) {
                double realHeight = event.getRealHeight();
                double height1 = realHeight * 0.42D;
                double height2 = realHeight * 0.75D;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height1, mc.thePlayer.posZ, mc.thePlayer.onGround));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height2, mc.thePlayer.posZ, mc.thePlayer.onGround));

            }

            if (smooth.get()) {
                mc.timer.timerSpeed = 0.55F;
                (new Thread(() -> {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException var1) {
                        ;
                    }
                    stepping = false;
                    mc.timer.timerSpeed = 1.0F;
                })).start();
            } else {
                stepping = false;
            }
        }
    }
}
