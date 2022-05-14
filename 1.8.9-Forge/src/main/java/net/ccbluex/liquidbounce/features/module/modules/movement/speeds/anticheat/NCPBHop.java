/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NCPBHop extends SpeedMode {

    private int level = 1;
    private double moveSpeed = 0.2873;
    private double lastDist;
    private int timerDelay;

    public NCPBHop() {
        super("NCPBHop");
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1F;
        level = 0;
        //level = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically ? 1 : 4;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        moveSpeed = getBaseMoveSpeed();
        level = 0;
    }

    @Override
    public void onMotion() {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        if(mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY + 0.759E-5D,mc.thePlayer.posZ);
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {

        if(mc.thePlayer.onGround && MovementUtils.isMoving() && level == 2) {
            mc.thePlayer.motionY = 0.399399995803833;
            event.setY(0.399399995803833);
            this.moveSpeed = MovementUtils.getBaseMoveSpeed() * 1.8;
        }else if(level == 3) {
            double difference = 1 * (lastDist - 0.2873);
            moveSpeed = lastDist - difference;
        }else{
            if(mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically)
                level = 1;

            moveSpeed = lastDist - lastDist / 110;
        }

        if (MovementUtils.isMoving()) {
            ++this.level;
        }

        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        final MovementInput movementInput = mc.thePlayer.movementInput;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if(forward == 0.0f && strafe == 0.0f) {
            event.setX(0.0);
            event.setZ(0.0);
        }else if(forward != 0.0f) {
            if(strafe >= 1.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            }else if(strafe <= -1.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
                strafe = 0.0f;
            }
            if(forward > 0.0f) {
                forward = 1.0f;
            }else if(forward < 0.0f) {
                forward = -1.0f;
            }
        }

        final double mx2 = Math.cos(Math.toRadians(yaw + 90.0f));
        final double mz2 = Math.sin(Math.toRadians(yaw + 90.0f));
        event.setX((double) forward * moveSpeed * mx2 + (double) strafe * moveSpeed * mz2);
        event.setZ((double) forward * moveSpeed * mz2 - (double) strafe * moveSpeed * mx2);

        mc.thePlayer.stepHeight = 0.6F;
        if(forward == 0.0F && strafe == 0.0F) {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if(mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
    }

    private double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}