/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

import java.util.List;

public class Hypixel extends SpeedMode {
    public static int stage;
    public double movementSpeed;
    boolean collided = false;
    boolean lessSlow;
    double less;
    private static final double distance = 0;

    public Hypixel() {
        super("Hypixel");
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }

    private double defaultSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return baseSpeed;
    }


    @Override
    public void onEnable() {
        boolean player = mc.thePlayer == null;
        collided = player ? false : mc.thePlayer.isCollidedHorizontally;
        lessSlow = false;
        less = 0.0;
        stage = 2;
        mc.timer.timerSpeed = 1;
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
    }

    public static double getBaseMovementSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return baseSpeed;
    }

    private boolean canZoom() {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }

    @Override
    public void onMove(MoveEvent e) {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);

        if (canZoom() && stage == 1) {
            movementSpeed = 1.66 * getBaseMovementSpeed() - 0.01;
            mc.timer.timerSpeed = speed.speedtimerValue.get();
        } else if (canZoom() && stage == 2) {
            mc.thePlayer.motionY = speed.speedYValue.get();
            e.setY(speed.speedYValue.get());
            movementSpeed *= 1.63;
            mc.timer.timerSpeed = speed.speedtimerValue.get();
        } else if (stage == 3) {
            double difference = 0.66 * (distance - getBaseMovementSpeed());
            movementSpeed = distance - difference;
            mc.timer.timerSpeed = speed.speedtimerValue.get();
        } else {
            List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0));
            if (collidingList.size() > 0 || mc.thePlayer.isCollidedVertically && stage > 0) {
                stage = MovementUtils.isMoving() ? 1 : 0;
            }
            movementSpeed = distance - distance / 159.0;
        }
        movementSpeed = Math.max(movementSpeed, getBaseMovementSpeed());
        setMoveSpeed(e,movementSpeed);
        if (MovementUtils.isMoving()) {
            ++stage;
        }
    }
    public void setMoveSpeed(final MoveEvent event, final double speed) {
        double forward = (double) this.mc.thePlayer.movementInput.moveForward;
        double strafe = (double) this.mc.thePlayer.movementInput.moveStrafe;
        float yaw = this.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians((double) (yaw + 90.0f)))
                    + strafe * speed * Math.sin(Math.toRadians((double) (yaw + 90.0f))));
            event.setZ(forward * speed * Math.sin(Math.toRadians((double) (yaw + 90.0f)))
                    - strafe * speed * Math.cos(Math.toRadians((double) (yaw + 90.0f))));
        }
    }
}
