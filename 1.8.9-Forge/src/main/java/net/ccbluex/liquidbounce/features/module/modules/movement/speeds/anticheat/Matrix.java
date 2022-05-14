/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;


import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class Matrix extends SpeedMode {
    public Matrix() { super("Matrix"); }

    @Override
    public void onUpdate() {
        if (mc.thePlayer.isInWater()) return;
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.speedInAir = 0.02098f;
                mc.timer.timerSpeed = 1.055f;
            } else {
                MovementUtils.strafe(MovementUtils.getSpeed());
            }
        } else {
            mc.timer.timerSpeed = 1f;
        }
    }
    public void onMotion() {}
    public void onMove(final MoveEvent event) {}
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02f;
        mc.timer.timerSpeed = 1f;
    }
}



